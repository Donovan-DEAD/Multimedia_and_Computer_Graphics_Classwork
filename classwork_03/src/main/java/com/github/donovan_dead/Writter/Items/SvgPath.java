package com.github.donovan_dead.Writter.Items;

import java.util.List;

public class SvgPath extends SvgComponent {
    private List<PathPoint> pathPoints;

    private Float fillOpacity;
    private String fill;

    private Integer strokeWidth;
    private Float strokeOpacity;
    private String stroke;

    public SvgPath(List<PathPoint> pathPoints) {
        this.pathPoints = pathPoints;
    }

    public SvgPath(List<PathPoint> pathPoints, Float fillOpacity, String fill, Integer strokeWidth, Float strokeOpacity, String stroke) {
        this.pathPoints = pathPoints;
        this.fillOpacity = fillOpacity == null || fillOpacity < 0  || fillOpacity > 1 ? 1 : fillOpacity;
        this.fill = fill;
        this.strokeWidth = strokeWidth == null || strokeWidth < 0 ? 1 : strokeWidth;
        this.strokeOpacity = strokeOpacity == null || strokeOpacity < 0  || strokeOpacity > 1 ? 1 : strokeOpacity;
        this.stroke = stroke;
    }

    @Override
    public byte[] Data() {
        StringBuilder dAttribute = new StringBuilder();
        for (PathPoint point : pathPoints) {
            dAttribute.append(point.toPathString()).append(" ");
        }

        StringBuilder finalString = new StringBuilder(100);
        finalString.append("<path d=\"").append(dAttribute.toString().trim()).append("\" ");

        if(fill != null) finalString.append("fill=\"").append(fill).append("\" ");
        if(fillOpacity != null) finalString.append("fill-opacity=\"").append(fillOpacity).append("\" ");

        if(strokeWidth != null) finalString.append("stroke-width=\"").append(strokeWidth).append("\" ");
        if(strokeOpacity != null) finalString.append("stroke-opacity=\"").append(strokeOpacity).append("\" ");
        if(stroke != null) finalString.append("stroke=\"").append(stroke).append("\" ");

        finalString.append("/>");

        return finalString.toString().getBytes();
    }
}
