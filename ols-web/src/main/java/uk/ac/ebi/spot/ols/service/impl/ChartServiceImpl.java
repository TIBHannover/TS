package uk.ac.ebi.spot.ols.service.impl;

import com.zavtech.morpheus.frame.DataFrame;
import com.zavtech.morpheus.util.Bounds;
import com.zavtech.morpheus.viz.chart.Chart;
import uk.ac.ebi.spot.ols.model.chart.ChartData;
import uk.ac.ebi.spot.ols.model.chart.ChartRequest;
import uk.ac.ebi.spot.ols.model.ontology.CharacteristicsType;
import uk.ac.ebi.spot.ols.model.ontology.ExtendedOntology;
import uk.ac.ebi.spot.ols.model.ontology.PairwiseSimilarity;
import uk.ac.ebi.spot.ols.service.ChartService;
import uk.ac.ebi.spot.ols.service.SimilarityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChartServiceImpl implements ChartService {
    private static final MediaType MIME_TYPE = MediaType.IMAGE_PNG;
    public static final int HEIGHT = 900;
    public static final int WIDTH = 800;

    private final SimilarityService similarityService;

    @Autowired
    public ChartServiceImpl(SimilarityService similarityService) {
        this.similarityService = similarityService;
    }

    @Override
    public ChartData chart(Optional<List<String>> ids, Optional<String> collection, ChartRequest request, Pageable pageable) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        Page<PairwiseSimilarity> page = ids.isPresent() && ids.get().size() == 1
            ? similarityService.getPairwiseSimilarity(ids.get().get(0), collection, pageable)
            : similarityService.getPairwiseSimilarity(ids, collection, pageable);

        List<PairwiseSimilarity> similarities = page.getContent();

        int height = request.getHeight().orElse(HEIGHT);
        int width = request.getWidth().orElse(WIDTH);

        List<String> names = similarities.stream()
            .map(similarity ->
                String.format("%s-%s, %.2f%%",
                    similarity.getPair().getFirst(), similarity.getPair().getSecond(), similarity.getPercent()
                )
            )
            .collect(Collectors.toList());

        DataFrame<String, String> frame = getDataFrame(names, similarities);
        drawChart(frame, request, "TS ontologies", os, width, height);

        return ChartData.builder()
            .data(os.toByteArray())
            .contentType(MIME_TYPE)
            .build();
    }

    @Override
    public <T extends ExtendedOntology> ChartData chart(T ontology, Optional<String> collection, ChartRequest request, Pageable pageable) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        Page<PairwiseSimilarity> page = similarityService.getPairwiseSimilarity(ontology, collection, pageable);
        List<PairwiseSimilarity> similarities = page.getContent();

        int height = request.getHeight().orElse(HEIGHT);
        int width = request.getWidth().orElse(WIDTH);

        List<String> names = similarities.stream()
            .map(similarity ->
                String.format("%s-%s, %.2f%%",
                    similarity.getPair().getFirst(), similarity.getPair().getSecond(), similarity.getPercent()
                )
            )
            .collect(Collectors.toList());

        DataFrame<String, String> frame = getDataFrame(names, similarities);
        drawChart(frame, request, ontology.getOntologyId() + " with TS ontologies", os, width, height);

        return ChartData.builder()
            .data(os.toByteArray())
            .contentType(MIME_TYPE)
            .build();
    }

    private DataFrame<String, String> getDataFrame(List<String> names, List<PairwiseSimilarity> similarities) {
        return DataFrame.of(
            names,
            String.class,
            columns ->
                Stream.of(CharacteristicsType.values())
                    .map(CharacteristicsType::name)
                    .map(String::toLowerCase)
                    .forEach(type ->
                        columns.add(
                            type,
                            similarities.stream()
                                .map(PairwiseSimilarity::getCharacteristics)
                                .map(map -> map.get(type).getPercent())
                                .collect(Collectors.toList())
                        )
                    )
        );
    }

    private void drawChart(DataFrame<String, String> frame, ChartRequest request, String subtitle, ByteArrayOutputStream os, int width, int height) {
        Chart.create().withBarPlot(frame, true, chart -> {
            chart.plot().axes().domain().label()
                .withFont(new Font("Arial", Font.BOLD, 13))
                .withText("Ontologies pairs");
            chart.plot().axes().range(0).withRange(Bounds.of(0, 100)).label().withText("Similarity percentage");
            if (request.getHorizontal().isPresent() && Boolean.TRUE.equals(request.getHorizontal().get())) {
                chart.plot().orient().horizontal();
            }
            chart.title().withText("Pairwise similarity");
            chart.subtitle().withText(subtitle);
            chart.legend().on();
            chart.writerPng(os, width, height, false);
        });
    }
}
