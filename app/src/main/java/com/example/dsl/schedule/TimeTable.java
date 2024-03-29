package com.example.dsl.schedule;

import static com.example.dsl.DSLUtil.print;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dsl.DSLUtil;
import com.example.dsl.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

public class TimeTable implements ViewTreeObserver.OnGlobalLayoutListener, TSListAdaptor.TSListener {
    public GridLayout BaseTable;
    public GridLayout UITable;
    private int TableWidth;
    private int TableHeight;
    private int Cell_Height=0;
    private int Cell_Width=0;
    private int TableColCount;
    private int TableRowCount;
    private int CellHeightWeight=0;
    private LinearLayout BaseTablePosition;
    private LinearLayout UITablePosition;
    private int TIME_INTERVAL=5;
    private final int DEFALUT_ROW=11;
    private int colstart =0;
    private Context context;
    private LinkedList<TextView> stikerlist = new LinkedList<>();
    private ArrayList<AdaptorDataSet> initStickers;
    public final String[] dayarray=new String[]{"월요일","화요일","수요일","목요일","금요일","토요일","일요일"};
    private ArrayList<AdaptorDataSet> LegacyStickers;
    public TimeTable(Context context,LinearLayout BaseTablePosition,LinearLayout UITablePosition){
        this.context=context;
        this.BaseTablePosition=BaseTablePosition;
        this.UITablePosition=UITablePosition;
        init(9);
    }
    public TimeTable(Context context,LinearLayout BaseTablePosition,LinearLayout UITablePosition, int CellHeightWeight){
        this.context=context;
        this.BaseTablePosition=BaseTablePosition;
        this.UITablePosition=UITablePosition;
        this.CellHeightWeight=CellHeightWeight;
        init(9);
    }
    //미리 설정할 정보가 있을 시 설정할 정보랑 초기화할 정보 필요.
    public void setLegacyStickers(ArrayList<AdaptorDataSet> LegacyStickers,TSListAdaptor Adaptor){
        this.LegacyStickers=LegacyStickers;
        this.initStickers=Adaptor.getArrayData();
    }
    public ArrayList<AdaptorDataSet> getLefacyStickers(){
        return LegacyStickers;
    }

    private void onReady(){
        if(LegacyStickers!=null){
            ChangeListener(initStickers);
        }
    }

    private void init(int timestart){
        addLayout(11,6,timestart);
    }
    private void addLayout(int rowcount,int colcount,int timestart){
        this.colstart =timestart;
        TableRowCount=rowcount;
        TableColCount=colcount;

        BaseTable=CreateGridLayout(TableColCount,TableRowCount);
        BaseTablePosition.addView(BaseTable);

        UITable=CreateGridLayout(TableColCount,TableRowCount);
        UITablePosition.addView(UITable);
        UITable.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }
    private GridLayout CreateGridLayout(int tablecol, int tablerow){
        GridLayout grid=new GridLayout(context);
        grid.setColumnCount(tablecol);
        grid.setRowCount(tablerow);
        GridLayout.LayoutParams gridParam=new GridLayout.LayoutParams();
        gridParam.height=GridLayout.LayoutParams.MATCH_PARENT;
        gridParam.width=GridLayout.LayoutParams.MATCH_PARENT;
        //각 셀마다 MARGIN이 따로 적용됨
        grid.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        gridParam.setMargins(0,0,0,0);
        grid.setLayoutParams(gridParam);
        grid.setOrientation(GridLayout.VERTICAL);
        return grid;
    }
    private void initUILayout(GridLayout grid){
        TextView v;
        int time;
        for(int rowc=0;rowc<TableRowCount;rowc++){
            for(int colc=0;colc<TableColCount;colc++){
                v=new TextView(context);
                v.setBackground(getItemsBGWithoutBorder(getItemsRadius(rowc,colc)));
                GridLayout.LayoutParams params= getItemLayoutParams(rowc,colc);
                params.setGravity(Gravity.CENTER);
                if(rowc==0 && colc>0){
                    v.setText(dayarray[colc-1]);
                    //textview parms gravity not working
                    v.setGravity(Gravity.CENTER);
                    UITable.addView(v,params);
                }else if(rowc>0&&colc==0){
                    time=(rowc+(colstart -1))%12;
                    if(time==0){
                        time=12;
                    }
                    v.setText(Integer.toString(time));
                    v.setGravity(Gravity.TOP|Gravity.RIGHT);
                    v.setPadding(0,(int)DPtoPX(5),(int)DPtoPX(5),0);
                    UITable.addView(v,params);
                }
            }
        }
    }
    private void initBaseLayout(GridLayout grid){
        View v;
        GridLayout.Spec row,col;
        //cell setting init
        for(int rowc=0;rowc<TableRowCount;rowc++){
            for(int colc=0;colc<TableColCount;colc++){
                v=new View(context);
                v.setBackground(getItemsBGWithBorder(getItemsRadius(rowc,colc)));
                row=GridLayout.spec(rowc,1);
                col=GridLayout.spec(colc,1);
                GridLayout.LayoutParams gridChildparams=new GridLayout.LayoutParams(row,col);
                gridChildparams.width=Cell_Width;
                gridChildparams.height=Cell_Height;
                grid.addView(v,gridChildparams);
            }
        }
    }
    private GridLayout.LayoutParams getItemLayoutParams(int rowindex, int colindex){
        GridLayout.LayoutParams gridChildparams=new GridLayout.LayoutParams(GridLayout.spec(rowindex,1),GridLayout.spec(colindex,1));
        gridChildparams.width=Cell_Width;
        gridChildparams.height=Cell_Height;
        return gridChildparams;
    }
    private float[] getItemsRadius(int rowcount,int colcount){
        float[] radi=new float[8];

        if(colcount==0){
            if(rowcount==0){
                radi[0]=20;
                radi[1]=20;
            }else if(rowcount==TableRowCount-1){
                radi[6]=20;
                radi[7]=20;
            }
        }else if(colcount==TableColCount-1){
            if(rowcount==0){
                radi[2]=20;
                radi[3]=20;
            }else if(rowcount==TableRowCount-1){
                radi[4]=20;
                radi[5]=20;
            }
        }
        return radi;
    }
    private float DPtoPX(float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    private GradientDrawable getItemsBGWithBorder(float[] radius) {
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setShape(GradientDrawable.RECTANGLE);
        gdDefault.setStroke((int)DPtoPX(1), Color.BLACK);
        gdDefault.setCornerRadii(radius);
        return gdDefault;
    }
    private GradientDrawable getItemsBGWithoutBorder(float[] radius){
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setShape(GradientDrawable.RECTANGLE);
        gdDefault.setCornerRadii(radius);
        return gdDefault;
    }
    private GradientDrawable getItemsBGWithoutBorder(float[] radius,int color){
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setShape(GradientDrawable.RECTANGLE);
        gdDefault.setCornerRadii(radius);
        gdDefault.setColor(color);
        return gdDefault;
    }
    @Override
    public void onGlobalLayout() {
        //if trigger not remove, it is called View any change
        BaseTable.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        TableHeight=BaseTable.getHeight();
        TableWidth=BaseTable.getWidth();
        if(Cell_Width==0){
            Cell_Width=getCell_Width();
        }
        if(CellHeightWeight==0){
            Cell_Height=TableHeight/DEFALUT_ROW;
        }else{
            Cell_Height=TableHeight/CellHeightWeight;
        }
        initBaseLayout(BaseTable);
        initUILayout(UITable);
        onReady();
    }
    @Override
    public void ChangeListener(ArrayList<AdaptorDataSet> list) {
        //첫번째와 마지막은 시간과 관련 없음
        Clear();
        int lastrecodeday=-1;
        //초기 시작시간부터 행이 생성되기 위함
        int firststart=colstart*100;
        //초기 시작시간에서 기본 행만큼 시작되기 위함
        int lastend=(DEFALUT_ROW-1+ colstart)*100;
        int day=0;
        int start=0;
        int end=0;
        //새로운 스티커 행열 계산
        for(int i=1;i<list.size()-1;i++){
            //starttime
            start=list.get(i).start;
            if(firststart>start){
                firststart=start;
            }
            //end time
            end=list.get(i).end;
            if(lastend<end){
                lastend=end;
            }
            day=list.get(i).day+1;
            if(lastrecodeday<day){
                lastrecodeday=day;
            }
        }
        //기존 스티커 행열 계산
        if(LegacyStickers!=null){
            for(int i=1;i<LegacyStickers.size()-1;i++){
                //starttime
                start=LegacyStickers.get(i).start;
                if(firststart>start){
                    firststart=start;
                }
                //end time
                end=LegacyStickers.get(i).end;
                if(lastend<end){
                    lastend=end;
                }
                day=LegacyStickers.get(i).day+1;
                if(lastrecodeday<day){
                    lastrecodeday=day;
                }
            }
        }
        //최종 행열 처리
        if(lastrecodeday>5&&TableColCount-1<lastrecodeday){
            for(int i2=TableColCount-1;i2<lastrecodeday;i2++){
                addColumnBaseLayout();
            }
            UITableReset();
            BaseTableUpdate();
        }else if(5<TableColCount-1&&lastrecodeday<TableColCount-1){
            for(int i2=TableColCount-1;lastrecodeday<i2&&i2>5;i2--){
                removeColBaseLayout();
            }
            UITableReset();
        }

        int[] colorary=new int[]{
                context.getColor(R.color.default_green),
                context.getColor(R.color.default_green)
        };
        if(LegacyStickers!=null){
            addSticker(LegacyStickers,firststart,lastend,colorary);
        }
        /*colorary=new int[]{
                context.getColor(R.color.default_green),
                context.getColor(R.color.default_green)
        };*/
        addSticker(list,firststart,lastend,colorary);
        UITimeUpdate(firststart/100);
        BaseTableUpdate();
        colorary=new int[]{
                DSLUtil.ScheduleColorList.getColor(context,0),
                DSLUtil.ScheduleColorList.getColor(context,1),
                DSLUtil.ScheduleColorList.getColor(context,2),
                DSLUtil.ScheduleColorList.getColor(context,3),
                DSLUtil.ScheduleColorList.getColor(context,4)
        };
        for(int i=(TableColCount+TableRowCount)-2;i<UITable.getChildCount();i++){
            (UITable.getChildAt(i)).setBackgroundColor(colorary[i%colorary.length]);
        }
    }
    private void addSticker(ArrayList<AdaptorDataSet> list,int firststart, int lastend, int[] colorary){
        int day;
        int start;
        int end;
        Calendar clacbonus=Calendar.getInstance();
        StringBuilder sb=new StringBuilder();
        String subject;
        String professor;
        for(int i=1;i<list.size()-1;i++){
            subject = list.get(i).subject;
            professor=list.get(i).professor;
            if(subject!=null){
                sb.append(subject);
                sb.append("\n");
            }
            if(professor!=null&&!professor.isEmpty()){
                sb.append(professor);
                sb.append("\n");
            }
            String place=list.get(i).place;
            //day
            day=list.get(i).day+1;
            //start time
            start=list.get(i).start;
            //end time
            end=list.get(i).end;

            int starthour=start/100;
            int startminute=start%100;
            int endhour=end/100;
            int endminute=end%100;

            float cellbasic=((float)Cell_Height)/((60/TIME_INTERVAL));
            clacbonus.set(Calendar.HOUR_OF_DAY,endhour);
            clacbonus.set(Calendar.MINUTE,endminute);
            clacbonus.add(Calendar.HOUR_OF_DAY,-starthour);
            clacbonus.add(Calendar.MINUTE,-startminute);
            int celltime=(clacbonus.get(Calendar.HOUR_OF_DAY)*100)+clacbonus.get(Calendar.MINUTE);
//            print(" cell "+celltime);
            int cellminute=celltime%100;
            int cellhour=celltime/100;
//            print((cellbasic*(cellminute/TIME_INTERVAL))+" "+(cellhour*12*cellbasic));
            float Customcellsize=((cellbasic*(cellminute/TIME_INTERVAL))+(cellhour*12*cellbasic));
            int cellgridheight=getNeedCol(starthour,endhour,endminute);
            //시간 0부터 시작하여 1시간 오를때마다 1씩 증가
            int CellStart=0;
            int needcol=getNeedCol(firststart/100,lastend/100,lastend%100);
            int limit=needcol-(TableRowCount-1);
//            print("start : "+start+" end "+end+" fiststart : "+firststart+" lastend : "+lastend+" Cell start : "+(start-firststart)/100+" Cell_Height "+Cell_Height);
//            print("cellbasic "+cellbasic+" cellhour "+cellhour+" cellminute "+cellminute+" Customcellsize "+Customcellsize);
//            print("limit:"+limit+" lastend "+lastend+" need col "+needcol);
            if(limit>0){
                for(int i2=0;i2<limit;i2++){
                    addRowBaseLayout();
                }
            }
            if(limit<0){
                for(int i2=limit;i2<0;i2++){
                    removeRowBaseLayout();
                }
            }
            CellStart=(start-firststart)/100;
//            print("cellminute "+cellminute);
            TextView stiker=new TextView(context);
            stiker.setBackground(getItemsBGWithoutBorder(getItemsRadius(CellStart+cellgridheight,day),colorary[i%colorary.length]));
//            print(" Cellstart "+(CellStart+1)+" cellsize "+(cellgridheight)+"");
            GridLayout.LayoutParams params= new GridLayout.LayoutParams(GridLayout.spec(CellStart+1,cellgridheight,GridLayout.TOP),GridLayout.spec(day,1,GridLayout.CENTER));
            params.width=Cell_Width;
            params.height=(int)Customcellsize;
//            print("margin "+(((start%100)/TIME_INTERVAL)*cellbasic));
            if(startminute!=0){
                params.setMargins(0,(int)((startminute/TIME_INTERVAL)*cellbasic),0,0);
            }
            stiker.setLayoutParams(params);
            if(place!=null){
                sb.append(place);
            }
            stiker.setText(sb.toString());
            sb.setLength(0);
            UITable.addView(stiker,UITable.getChildCount());
            stikerlist.add(stiker);
        }
    }
    private int getNeedCol(int starthour,int endhour, int endminute){
        int cellgridheight=endhour-starthour;
        if(starthour!=endhour){
            if((endminute)>0){
                cellgridheight+=1;
            }
        }else{
            cellgridheight+=1;
        }
        return cellgridheight;
    }
    private void UITimeUpdate(int starttime){
        for(int i2=TableColCount-1;i2<TableRowCount+TableColCount-2;i2++){
            int time=(i2-TableColCount+1)+starttime;
            time=time%12;
            if(time==0){
                time=12;
            }
            ((TextView)UITable.getChildAt(i2)).setText(time+"");
        }
    }
    private void removeColBaseLayout(){
        for(int i=TableRowCount;i>0;i--){
            BaseTable.removeViewAt((TableColCount*(i))-1);
        }
        UITable.removeViewAt(TableColCount-2);
        TableColCount-=1;
        UITable.setColumnCount(TableColCount);
        BaseTable.setColumnCount(TableColCount);
        Cell_Width=getCell_Width();
    }
    private void removeRowBaseLayout(){
        for(int i=1;i<TableColCount+1;i++){
            BaseTable.removeViewAt((TableRowCount*TableColCount)-i);
        }
        UITable.removeViewAt(TableColCount-1+TableRowCount-2);
        TableRowCount-=1;
        UITable.setRowCount(TableRowCount);
        BaseTable.setRowCount(TableRowCount);
    }

    private void addRowBaseLayout(){

        TableRowCount+=1;
        UITable.setRowCount(TableRowCount);
        BaseTable.setRowCount(TableRowCount);
        for(int i=0;i<TableColCount;i++){
            View e=new View(context);
            BaseTable.addView(e,((TableRowCount-1)*TableColCount)+i,getItemLayoutParams(TableRowCount-1,i));
        }

        TextView tv=new TextView(context);
        GridLayout.LayoutParams params=getItemLayoutParams((TableRowCount-1),0);
        params.setGravity(Gravity.TOP|Gravity.RIGHT);
        tv.setGravity(Gravity.TOP|Gravity.RIGHT);
        tv.setPadding(0,(int)DPtoPX(5),(int)DPtoPX(5),0);
        UITable.addView(tv,TableColCount-1+TableRowCount-2,params);

    }
    private void addColumnBaseLayout(){
        TableColCount+=1;
        UITable.setColumnCount(TableColCount);
        BaseTable.setColumnCount(TableColCount);
        Cell_Width=getCell_Width();

        for(int i=0;i<TableRowCount;i++){
            View e=new View(context);
            BaseTable.addView(e,(TableColCount*(i+1))-1,getItemLayoutParams(i,TableColCount-1));
//            print((TableColCount*(i+1))-1+"");
        }
        //UITABLE에 textview 새로 만들시 column추가
        TextView tv=new TextView(context);
        tv.setText(dayarray[TableColCount-2]);
//        print(TableColCount);
        GridLayout.LayoutParams params=getItemLayoutParams(0,TableColCount-1);
        params.setGravity(Gravity.CENTER);
        params.setMargins(0,0,0,0);
        tv.setGravity(Gravity.CENTER);
        tv.setWidth(Cell_Width);
        UITable.addView(tv,TableColCount-2,params);
    }

    private void BaseTableUpdate(){
        Cell_Width=getCell_Width();
        View v;
        for(int i=0;i<BaseTable.getChildCount();i++){
            v=BaseTable.getChildAt(i);
            v.getLayoutParams().width=Cell_Width;
            if(v.getBackground()!=null){
                if(i==0){
                    ((GradientDrawable)v.getBackground()).setCornerRadii(getItemsRadius(0,0));
                }else if(i==TableColCount-1){
                    ((GradientDrawable)v.getBackground()).setCornerRadii(getItemsRadius(0,TableColCount-1));
                }else if(i==(TableRowCount-1)*TableColCount){
                    ((GradientDrawable)v.getBackground()).setCornerRadii(getItemsRadius(TableRowCount-1,0));
                }else if(i==(TableRowCount*TableColCount)-1){
                    ((GradientDrawable)v.getBackground()).setCornerRadii(getItemsRadius(TableRowCount-1,TableColCount-1));
                }else{
                    ((GradientDrawable)v.getBackground()).setCornerRadii(getItemsRadius(1,1));
                }
            }
            else{
                if(i==0){
                    v.setBackground(getItemsBGWithBorder(getItemsRadius(0,0)));
                }else if(i==TableColCount-1){
                    v.setBackground(getItemsBGWithBorder(getItemsRadius(0,TableColCount-1)));
                }else if(i==(TableRowCount-1)*TableColCount){
                    v.setBackground(getItemsBGWithBorder(getItemsRadius(TableRowCount-1,0)));
                }else if(i==(TableRowCount*TableColCount)-1){
                    v.setBackground(getItemsBGWithBorder(getItemsRadius(TableRowCount-1,TableColCount-1)));
                }else{
                    v.setBackground(getItemsBGWithBorder(getItemsRadius(1,1)));
                }
            }

        }
    }
    private void UITableReset(){
        for(int i=0;i<UITable.getChildCount();i++){
            GridLayout.LayoutParams ee=(GridLayout.LayoutParams)UITable.getChildAt(i).getLayoutParams();
            ee.width=getCell_Width();
            //ss;
            UITable.getChildAt(i).setLayoutParams(ee);
        }
    }

    private void Clear(){
        for(int i = 0; i< stikerlist.size(); i++){
            UITable.removeView(stikerlist.get(i));
        }
        stikerlist.clear();
    }
    private int getCell_Width(){
        Cell_Width=(TableWidth/TableColCount);
        return Cell_Width;
    }
    public int getTableColCount(){
        return TableColCount;
    }
    public int getTableRowCount(){
        return TableRowCount;
    }
}
