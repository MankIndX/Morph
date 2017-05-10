package com.mankind.morph;

/**
 * @author xiaomingkai
 *         2017/4/27
 */
public final class Morph {

    private static final String FILTER_AUTO_ORIENT = "auto-orient";
    private static final String FILTER_THUMBNAIL = "thumbnail";
    private static final String FILTER_FORMAT = "format";
    private static final String FILTER_STRIP = "strip";
    private static final String FILTER_BLUR = "blur";
    private static final String FILTER_SHARPEN = "sharpen";
    private static final String FILTER_QUALITY = "quality";

    private String imageUrl;
    private boolean autoOrient;
    private int resizeWidth;
    private int resizeHeight;
    private boolean hasResize;
    private boolean strip;
    private int blurRadius;
    private int blurSigma;
    private boolean sharpen;
    private int quality;
    private ImageFormat imageFormat = ImageFormat.WEBP;

    public static Morph buildImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("image url must not be blank");
        }
        return new Morph(imageUrl);
    }

    private Morph(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 根据原图 EXIF 信息自动旋正
     */
    public Morph autoOrient() {
        this.autoOrient = true;
        return this;
    }

    /**
     * width,height 都不为0时，等比缩放，比例值为宽缩放比和高缩放比的较小值
     * width == 0，height 固定，width 等比缩放
     * height == 0， width 固定， height 等比缩放
     */
    public Morph resize(int width, int height) {
        if (width < 0) {
            throw new IllegalArgumentException("Width must be positive number or 0.");
        }
        if (height < 0) {
            throw new IllegalArgumentException("Height must be positive number or 0.");
        }
        if (width == 0 && height == 0) {
            throw new IllegalArgumentException("At least one dimension has to be positive number.");
        }
        hasResize = true;
        this.resizeWidth = width;
        this.resizeHeight = height;
        return this;
    }

    /**
     * 图片格式，默认为 WEBP
     *
     * @see ImageFormat
     */
    public Morph imageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    /**
     * 图片质量，仅支持 jpg
     */
    public Morph quality(int quality) {
        if (quality < 1 || quality > 100) {
            throw new IllegalArgumentException("quality must be within [1, 100]");
        }
        this.quality = quality;
        return this;
    }

    /**
     * 去除图片中的元信息
     */
    public Morph strip() {
        this.strip = true;
        return this;
    }

    /**
     * 高斯模糊，图片格式为gif时，不支持该参数。
     *
     * @param radius 模糊半径，范围1-50
     * @param sigma 正太分布的标准差，必须大于0
     */
    public Morph blur(int radius, int sigma) {
        if (radius < 1 || radius > 360) {
            throw new IllegalArgumentException("radius must be within [1, 360]");
        }
        if (sigma <= 0) {
            throw new IllegalArgumentException("sigma must greater than 0");
        }
        this.blurRadius = radius;
        this.blurSigma = sigma;
        return this;
    }

    /**
     * 锐化
     */
    public Morph sharpen() {
        this.sharpen = true;
        return this;
    }

    public String toUrl() {
        return imageUrl + assembleConfig();
    }

    /**
     * api:
     * https://developer.qiniu.com/dora/api/1270/the-advanced-treatment-of-images-imagemogr2
     */
    private String assembleConfig() {
        StringBuilder builder = new StringBuilder();
        builder.append("?imageMogr2/");
        if (autoOrient) {
            builder.append(FILTER_AUTO_ORIENT);
            builder.append("/");
        }

        if (hasResize) {
            doResize(builder);
        }

        builder.append(FILTER_FORMAT).append("/");
        builder.append(imageFormat.value).append("/");

        if (strip) {
            builder.append(FILTER_STRIP).append("/");
        }

        if (blurRadius != 0 && blurSigma != 0 && !ImageFormat.GIF.equals(imageFormat)) {
            builder.append(FILTER_BLUR).append("/");
            builder.append(blurRadius).append("x").append(blurSigma);
            builder.append("/");
        }

        if (imageFormat == ImageFormat.JPEG && quality > 0) {
            builder.append(FILTER_QUALITY).append(quality);
            builder.append("/");
        }

        if (sharpen) {
            builder.append(FILTER_SHARPEN);
        }
        return builder.toString();
    }

    private void doResize(StringBuilder builder) {
        builder.append(FILTER_THUMBNAIL).append("/");

        if (resizeWidth == 0) {
            // 指定图片高度，宽度等比缩小
            builder.append("9999x").append(resizeHeight);
        } else if (resizeHeight == 0) {
            // 指定图片宽度，高度等比缩小
            builder.append(resizeWidth).append("x9999");
        } else {
            // 等比缩小，比例值为宽缩放比和高缩放比的较小值
            builder.append(resizeWidth).append("x").append(resizeHeight);
        }
        builder.append(">/");
    }

    /** Image formats supported by Qiniu. */
    public enum ImageFormat {
        GIF("gif"), JPEG("jpeg"), PNG("png"), WEBP("webp");

        private final String value;

        ImageFormat(String value) {
            this.value = value;
        }
    }
}
