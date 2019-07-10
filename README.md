#android二维码生成器 艺术二维码

###先看图
<img src="http://img.blog.csdn.net/20160420030537857" width="400px" />
<img src="http://img.blog.csdn.net/20160420030628714" width="400px" />
<img src="http://img.blog.csdn.net/20160420030704640" width="400px" />
<img src="http://img.blog.csdn.net/20160420030752078" width="400px" />
<img src="http://img.blog.csdn.net/20160420030821001" width="400px" />
##说明
说明一点微信二维码识别和ZXING，ZBAR都不同 

![这里写图片描述](http://img.blog.csdn.net/20160420033220427)

测试发现 
微信中 定位区 的 点 有60%（大概）以上是被填充的 就能 识别为二维码 （方点，圆点，液化点都可识别）
ZXing中更具规格不同 有不一样 信息量较小的二维码 和微信一样（方点，圆点，液化点都可识别）
信息量较大的 由于出现的校正点 导致（方点，液化点可识别，圆点不可识别）
图像组成的二维码定位区中要选择合适的图像才行（试了好久才试出来的），Zxing不用想了 基本都不可识别
zbar：未测试


###关键代码：
确定个信息块的位置
``` java
     /**
     * 获取二维码位置信息
     *
     * @param matrix
     * @param rect 带回二维码边界
     * @return 返回单个信息点的宽
     */
    private static int checkParam(BitMatrix matrix, Rect rect) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();

        int startX = 0;
        int startY = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    startX = x;
                    startY = y;
                    x = width;
                    y = height;
                }
            }
        }
        int endX = 0;
        for (int x = width - 1; x >= 0; x--) {
            if (matrix.get(x, startY)) {
                endX = x;
                x = -1;
            }
        }
        int endY = 0;
        for (int y = height - 1; y >= 0; y--) {
            if (matrix.get(startX, y)) {
                endY = y;
                y = -1;
            }
        }
        int cellWidth = 1;
        while (true) {
            int pX = startX + cellWidth;
            int pY = startY + cellWidth;
            if (pX <= endX && pY <= endY && matrix.get(pX, pY)) {
                cellWidth++;
                continue;
            }
            break;
        }
        rect.left = startX;
        rect.top = startY;
        rect.right = endX;
        rect.bottom = endY;
        return cellWidth;
    }
```
上面的代码通过遍历 BitMatrix（也可以使用图片的像素点，可以直接用一个生成好的二维码来生成新的艺术二维码） 找到第一个true值点 （如果用像素点，就是找到第一个带色点），该点的坐标就是起始坐标，找到同行最后一个true值点，该点的x就是 结束点的x，找到同列最后一个true值点，该点的y就是 结束点的y；
从起始点开始，延对角线找，找到第一个false值点，该点到起始点的单一方向上的距离 就是 一个信息点的宽。

定位区的宽＝7个信息点的宽＝7*cellWidth；
通过定位区的宽可以避免修改到它们 或者 对它们使用不同的修改方式
通过信息点的宽 用来 找到每个信息点 并替换它们

液化二维码原理
判断某个信息点的周围点的情况，在对应角上加上圆弧；
如
某一信息点的左上没有信息点且同时左边和上面同时也没有信息点，说明该点左上角位凸角，要切出圆角；
某一信息点的AB角没有信息点且同时A面和B面同时也没有信息点，说明该点AB角位凸角，要切出圆角；

某一空白点的左边和上面同时具有信息点，说明该点左上角位凹角，要填充圆角；
某一空白点的A面和B面同时具有信息点，说明该点AB角位凹角，要填充圆角；


源码中 自定义的CaptureManager可以更加便捷的编写二维码扫描界面 解决CaptureActivity和java无法多继承带来的不便
