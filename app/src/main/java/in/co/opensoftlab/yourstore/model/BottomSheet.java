package in.co.opensoftlab.yourstore.model;

/**
 * Created by dewangankisslove on 20-12-2016.
 */

public class BottomSheet {
    private int titleId;
    private int imageId;

    public BottomSheet(int titleId, int imageId) {
        this.titleId = titleId;
        this.imageId = imageId;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
