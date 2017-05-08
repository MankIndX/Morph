# Morph
[![](https://jitpack.io/v/MankIndX/Morph.svg)](https://jitpack.io/#MankIndX/Morph)  <a href="http://www.methodscount.com/?lib=com.github.MankIndX%3AMorph%3A0.2.1"><img src="https://img.shields.io/badge/Methods and size-27 | 4 KB-e91e63.svg"/></a>

基于七牛图片处理 [api](https://developer.qiniu.com/dora/api/1270/the-advanced-treatment-of-images-imagemogr2) 生成所需图片 URIs

## Download
Gradle:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compile 'com.github.MankIndX:Morph:${latestVersion}'
}
```
## Supported

* 缩放
* 锐化
* 高斯模糊
* 根据原图 EXIF 信息，自动旋正
* 输出图片格式 jpg、gif、png、webp

## Usage

```java
String url = Morph.buildImage(imageUrl)
        .autoOrient()
        .resize(width, height)
        .imageFormat(ImageFormat.WEBP)
        .quality(80) // quality 仅支持 jpg
        .blur(radius, sigma)
        .sharp()
        .toUrl();
```

## License

    Copyright 2017 MankIndX

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

