package com.sample.moneymanager;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class FragmentStatistics extends Fragment implements SurfaceHolder.Callback {

    SurfaceView graph;
    SurfaceHolder surfaceHolder;
    int currentMonth;
    int currentYear;

    TextView monthText;
    ImageView prev;
    ImageView next;

    boolean pie = false;

    public static FragmentStatistics newInstance() {
        FragmentStatistics fragment = new FragmentStatistics();
        return fragment;
    }

    public FragmentStatistics() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_statistics, container, false);
        graph = (SurfaceView) v.findViewById(R.id.graph);
        graph.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("TOUCH");
                pie = !pie;
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_UP:
                        draw(surfaceHolder);
//                        break;
//                }
                return false;
            }
        });
        surfaceHolder = graph.getHolder();
        surfaceHolder.addCallback(this);

        monthText = (TextView) v.findViewById(R.id.stats_text);
        prev = (ImageView) v.findViewById(R.id.stats_prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPrevMonth();
            }
        });
        next = (ImageView) v.findViewById(R.id.stats_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextMonth();
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GregorianCalendar calendar = new GregorianCalendar();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        currentMonth = month;
        currentYear = year;
    }

    private void loadNextMonth() {
        if(currentMonth<11) {
            currentMonth++;
        } else {
            currentMonth=0;
            currentYear++;
        }

        String month = getMonthName(currentMonth);
        monthText.setText(month + " " + currentYear);
        draw(surfaceHolder);
    }

    private void loadPrevMonth() {
        if(currentMonth>0) {
            currentMonth--;
        } else {
            currentMonth=11;
            currentYear--;
        }

        String month = getMonthName(currentMonth);
        monthText.setText(month + " " + currentYear);
        draw(surfaceHolder);
    }

    private void draw(SurfaceHolder holder) {
        Database db = new Database(getActivity().getBaseContext());
        int transport = db.getSingleMonthlyCategoryExpense("Transport", currentMonth, currentYear);
        int food = db.getSingleMonthlyCategoryExpense("Food", currentMonth, currentYear);
        int utils = db.getSingleMonthlyCategoryExpense("Utilities", currentMonth, currentYear);
        int other = db.getSingleMonthlyCategoryExpense("Other", currentMonth, currentYear);
        db.close();
        db = null;

        int maxPrice = transport;
        if(food > maxPrice) {
            maxPrice = food;
        }
        if(utils > maxPrice) {
            maxPrice = utils;
        }
        if(other > maxPrice) {
            maxPrice = other;
        }
        maxPrice += 1000;
        Canvas canvas = holder.lockCanvas();

        if(!pie) {
            int columnRowValue = maxPrice / 10;

            String[] axisXtext = {"Food", "Utils", "Trans", "Other"};

            int width = canvas.getWidth();
            int height = canvas.getHeight();
            int axisStroke = 3;
            int padding = 50;

            int axisXvalues = (width - padding * 2) / 4;
            int axisYvalues = (height - padding * 2) / 10;

            Paint background = new Paint();
            background.setColor(Color.WHITE);
            canvas.drawRect(0, 0, width, height, background);

            Paint axis = new Paint();
            axis.setColor(Color.BLACK);
            axis.setStrokeWidth(axisStroke);
            axis.setStyle(Paint.Style.STROKE);
            canvas.drawLine(padding, height - padding, width - padding, height - padding, axis);
            canvas.drawLine(padding, padding, padding, height - padding, axis);

            Paint text = new Paint();
            text.setColor(Color.BLACK);
            //text.setStrokeWidth(2);
            //text.setStyle(Paint.Style.STROKE);

            Paint row = new Paint();
            row.setColor(Color.LTGRAY);
            row.setStrokeWidth(1);
            row.setStyle(Paint.Style.STROKE);

            for (int i = 0; i <= 10; i++) {
                int startY = height - padding - axisYvalues * i;
                int priceValue = columnRowValue * i;
                canvas.drawText("" + priceValue + "$", 10, startY, text);
                if (i == 0) {
                    continue;
                }
                canvas.drawLine(padding, startY, width - padding, startY, row);
            }

            Paint column = new Paint();
            for (int i = 0; i < 4; i++) {
                int c = i % 4;
                int v = 0;
                switch (c) {
                    case 0:
                        column.setColor(Color.RED);
                        v = food;
                        break;
                    case 1:
                        column.setColor(Color.GREEN);
                        v = utils;
                        break;
                    case 2:
                        column.setColor(Color.YELLOW);
                        v = transport;
                        break;
                    case 3:
                        column.setColor(Color.BLUE);
                        v = other;
                        break;
                    default:
                        v = 1000;
                        column.setColor(Color.LTGRAY);
                }
                int startX = padding + axisXvalues * i + 25;
                float percent = (float) v / (float) maxPrice;
                int calcHeight = (int) ((height - padding * 2) * percent);
                canvas.drawText(axisXtext[i], startX, height - padding + 15, text);

                int xLeft = startX;
                int yTop = height - padding - calcHeight;
                int xRight = startX + 25;
                int yBottom = height - padding;

                canvas.drawRect(xLeft, yTop, xRight, yBottom, column);
            }
        } else {
            int padding = 50;
            int sum = transport + food + utils + other;
//            System.out.println("sum:" + sum);
            float transportRatio = (float) transport / (float) sum;
            float foodRatio = (float) food / (float) sum;
            float utilsRatio = (float) utils / (float) sum;
            float otherRatio = (float) other / (float) sum;
//            System.out.println("transportRatio:" + transportRatio);
//            System.out.println("foodRatio:" + foodRatio);
//            System.out.println("utilsRatio:" + utilsRatio);
//            System.out.println("otherRatio:" + otherRatio);
            float transportAngle = 360 * transportRatio;
            float foodAngle = 360 * foodRatio;
            float utilsAngle = 360 * utilsRatio;
            float otherAngle = 360 * otherRatio;
//            System.out.println("transportAngle:" + transportAngle);
//            System.out.println("foodAngle:" + foodAngle);
//            System.out.println("utilsAngle:" + utilsAngle);
//            System.out.println("otherAngle:" + otherAngle);
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            Paint background = new Paint();
            background.setColor(Color.WHITE);
            canvas.drawRect(0, 0, width, height, background);

            Paint transportRect = new Paint();
            transportRect.setColor(Color.DKGRAY);
//            transportRect.setStrokeWidth(2);
//            transportRect.setStyle(Paint.Style.STROKE);

            Paint foodRect = new Paint();
            foodRect.setColor(Color.RED);
//            foodRect.setStrokeWidth(2);
//            foodRect.setStyle(Paint.Style.STROKE);

            Paint utilsRect = new Paint();
            utilsRect.setColor(Color.GREEN);
//            utilsRect.setStrokeWidth(2);
//            utilsRect.setStyle(Paint.Style.STROKE);

            Paint otherRect = new Paint();
            otherRect.setColor(Color.BLUE);
//            otherRect.setStrokeWidth(2);
//            otherRect.setStyle(Paint.Style.STROKE);

            int startAngle =0;
            RectF r = new RectF(padding, padding, width-padding, height-padding);
            canvas.drawArc(r, startAngle, transportAngle, true, transportRect);
            startAngle += transportAngle;
            canvas.drawArc(r, startAngle, foodAngle, true, foodRect);
            startAngle += foodAngle;
            canvas.drawArc(r, startAngle, utilsAngle, true, utilsRect);
            startAngle += utilsAngle;
            canvas.drawArc(r, startAngle, 360 - startAngle, true, otherRect);

            float h = (float) padding / 2;
            float startX = ((float)width - (float)25) / 6;
            System.out.println("h:" + h);
            System.out.println("startX:" + startX);
            canvas.drawText("Trans: " + transport + "$", startX, h, transportRect);
            canvas.drawText("Food: " + food + "$", startX * 2, h, foodRect);
            canvas.drawText("Utils: " + utils + "$", startX * 3, h, utilsRect);
            canvas.drawText("Other: " + other + "$", startX * 4, h, otherRect);
        }
        holder.unlockCanvasAndPost(canvas);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        draw(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        draw(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onStart() {
        super.onStart();
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onStop() {
        super.onStop();
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private String getMonthName(int month) {
        String name = "";
        switch(month) {
            case 0:
                name = "January";
                break;
            case 1:
                name = "February";
                break;
            case 2:
                name = "March";
                break;
            case 3:
                name = "April";
                break;
            case 4:
                name = "May";
                break;
            case 5:
                name = "June";
                break;
            case 6:
                name = "July";
                break;
            case 7:
                name = "August";
                break;
            case 8:
                name = "September";
                break;
            case 9:
                name = "October";
                break;
            case 10:
                name = "November";
                break;
            case 11:
                name = "December";
                break;
        }
        return name;
    }
}
