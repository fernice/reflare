package de.flare.render.rf;

import de.flare.render.RenderPipeline;
import de.flare.render.ResourceFactory;

public class RFRenderPipeline extends RenderPipeline {

    private static RFRenderPipeline instance;

    public static RFRenderPipeline getInstance() {
        if (instance == null) {
            instance = new RFRenderPipeline();
        }
        return instance;
    }

    private ResourceFactory resourceFactory;

    @Override
    public ResourceFactory getResourceFactory() {
        if (resourceFactory == null) {
            resourceFactory = new RFResourceFactory();
        }

        return resourceFactory;
    }

    @Override
    public boolean isLegacy() {
        return true;
    }

    @Override
    public boolean isCompatibilityMode() {
        return true;
    }

    private RFComponentPainter componentPainter;

    public RFComponentPainter getSharedComponentPainter() {
        return componentPainter;
    }
}
