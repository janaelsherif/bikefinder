package eu.bikefinder.app.web.dto;

import jakarta.validation.constraints.Size;

/** Optional focus text for Claude when generating the competitor brief. */
public class CompetitorWatchBriefRequest {

    @Size(max = 4000)
    private String focus;

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }
}
