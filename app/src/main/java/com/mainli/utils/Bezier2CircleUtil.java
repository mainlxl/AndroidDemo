package com.mainli.utils;

import android.graphics.Path;
import android.graphics.PointF;

/**
 * Created by Mainli on 2016/12/30.
 * 贝塞尔曲线绘制圆
 * 参考:http://stackoverflow.com/questions/1734745/how-to-create-circle-with-b%C3%A9zier-curves
 */
public final class Bezier2CircleUtil {
    public static final float C = 0.552284749831f; // 一个常量，用来计算绘制圆形贝塞尔曲线控制点的位置

    public static class BezierCircle {
        PointF center;//圆心
        PointF[] tops;//顶点
        PointF[] flags;//控制点
        float r;//半径
    }
    //-----------------------------------快速设置圆路径-----------------------------------------------------

    /**
     * @param r 半径
     */
    public static BezierCircle createCircle(float r) {
        return createCircle(new PointF(0f, 0f), r);
    }

    /**
     * @param point 圆心
     * @param r     半径
     */
    public static BezierCircle createCircle(PointF point, float r) {
        BezierCircle bezierCircle = new BezierCircle();
        bezierCircle.center = point;
        bezierCircle.r = r;
        bezierCircle.tops = obtianTopPoints(point, r);
        bezierCircle.flags = obtianFlagPoints(bezierCircle.tops);
        return bezierCircle;
    }


    //---------------------------------圆顶点计算------------------------------------------------

    /**
     * @param r 半径
     * @return 顶点坐标集合(圆心为 ( 0, 0)顺时针从最顶上开始)
     */
    public static PointF[] obtianTopPoints(float r) {
        return obtianTopPoints(new PointF(0f, 0f), r);
    }

    /**
     * @param point 圆心
     * @param r     半径
     * @return 顶点坐标集合(顺时针从最顶上开始)
     */
    public static PointF[] obtianTopPoints(PointF point, float r) {
        PointF[] circleTops = new PointF[4];//圆的顶点坐标 顺序为顺时针从最上边开始
        circleTops[0] = new PointF(point.x, point.y + r);
        circleTops[1] = new PointF(point.x + r, point.y);
        circleTops[2] = new PointF(point.x, -r + point.y);
        circleTops[3] = new PointF(-r + point.x, point.y);
        return circleTops;
    }
//-----------------------------------控制点计算-----------------------------------------------------

    public static PointF[] obtianFlagPoints(PointF[] tops) {
        if (tops.length != 4) throw new RuntimeException("mTops length illegal should 4");
        float mDifference = Math.abs(tops[1].y - tops[0].y) * C;// 圆形的控制点与数据点的差值
        PointF[] points = new PointF[8];//圆的顶点坐标 顺序为顺时针从最上边开始
        points[0] = new PointF(tops[0].x + mDifference, tops[0].y);//右上控制点
        points[1] = new PointF(tops[1].x, tops[1].y + mDifference);

        points[2] = new PointF(tops[1].x, tops[1].y - mDifference);//右下控制点
        points[3] = new PointF(tops[2].x + mDifference, tops[2].y);

        points[4] = new PointF(tops[2].x - mDifference, tops[2].y);//左下控制点
        points[5] = new PointF(tops[3].x, tops[3].y - mDifference);

        points[6] = new PointF(tops[3].x, tops[3].y + mDifference);//左上控制点
        points[7] = new PointF(tops[0].x - mDifference, tops[0].y);


        return points;
    }
//-----------------------------------路径链接-----------------------------------------------------

    /**
     * @param path 路径
     * @return path 路径
     */
    public static Path bezier3ToCircle(Path path, BezierCircle circle) {
        return bezier3ToCircle(path, circle.tops, circle.flags);
    }

    /**
     * @param path       路径
     * @param circleTops 顶点坐标
     * @param circleFlag 控制点坐标
     * @return path 路径
     */
    public static Path bezier3ToCircle(Path path, PointF[] circleTops, PointF[] circleFlag) {
        if (circleTops.length != 4)
            throw new RuntimeException("circleTops length illegal should 4");
        if (circleFlag.length != 8)
            throw new RuntimeException("circleFlag length illegal should 8");
        path.moveTo(circleTops[0].x, circleTops[0].y);
        for (int i = 0; i < 4; i++) {
            path.cubicTo(circleFlag[2 * i].x, circleFlag[2 * i].y, circleFlag[2 * i + 1].x, circleFlag[2 * i + 1].y, circleTops[(i + 1) % 4].x, circleTops[(i + 1) % 4].y);
        }
        return path;
    }

    //-----------------------------------修正控制点与定点绘制心形-----------------------------------------------------
    public static void fixHeart(BezierCircle circle) {
        fixHeart(circle.tops, circle.flags);
    }

    public static void fixHeart(PointF[] tops, PointF[] flags) {
        float r = Math.abs(tops[1].y - tops[0].y);
        tops[2].offset(0, 0.65f * r);//最上定点y向下偏移0.65
        float v = 0.1f * r;
        tops[1].offset(-v, 0);//左右定点向内偏移0.1
        tops[3].offset(v, 0);
        float v1 = 0.3f * r;
        flags[0].offset(0, -v1);//最下两个控制点向内偏移0.3
        flags[7].offset(0, -v1);
        float v2 = 0.2f * r;//最下两个控制点紧邻两个终止点向内偏移0.2向下偏移0.15
        flags[1].offset(-v2, -v1 / 2);
        flags[6].offset(v2, -v1 / 2);
    }

}