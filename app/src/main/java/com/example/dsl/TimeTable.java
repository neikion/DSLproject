package com.example.dsl;

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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class TimeTable implements ViewTreeObserver.OnGlobalLayoutListener, TSListAdaptor.TSListener {
    int TableWidth;
    int TableHeight;
    int Cell_Height=0;
    int Cell_Width=0;
    int TableColCount;
    int TableRowCount;
    int CellHeightWeight=0;
    GridLayout BaseTable;
    GridLayout UITable;
    LinearLayout BaseTablePosition;
    LinearLayout UITablePosition;
    int TIME_INTERVAL=5;
    final int DEFALUT_ROW=11;
    int colstart =0;
    Context context;
    LinkedList<TextView> stikerlist = new LinkedList<>();
    private ArrayList<TSListAdaptor.AdaptorDataSet> initStickers;
    public final String[] dayarray=new String[]{"월요일","화요일","수요일","목요일","금요일","토요일","일요일"};
    private ArrayList<TSListAdaptor.AdaptorDataSet> LegacyStickers;
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
    public void setLegacyStickers(ArrayList<TSListAdaptor.AdaptorDataSet> list){
        LegacyStickers=list;
    }
    public void setOnReadyStickers(ArrayList<TSListAdaptor.AdaptorDataSet> list){
        initStickers=list;
    }
    public ArrayList<TSListAdaptor.AdaptorDataSet> getLefacyStickers(){
        return LegacyStickers;
    }
    private void onReady(){
        if(LegacyStickers!=null){
            ChangeListener(initStickers);
        }
    }

    public void init(int timestart){
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
        //cell setting init
        for(int rowc=0;rowc<TableRowCount;rowc++){
            for(int colc=0;colc<TableColCount;colc++){
                v=new View(context);
                v.setBackground(getItemsBGWithBorder(getItemsRadius(rowc,colc)));
                additems(v, grid, rowc, colc);
            }
        }
    }
    private GridLayout.LayoutParams getItemLayoutParams(int rowindex, int colindex){
        GridLayout.LayoutParams gridChildparams=new GridLayout.LayoutParams(GridLayout.spec(rowindex,1),GridLayout.spec(colindex,1));
        gridChildparams.width=Cell_Width;
        gridChildparams.height=Cell_Height;
        return gridChildparams;
    }
    private void additems(View Items, GridLayout grid, int rowcount, int colcount){
        GridLayout.Spec row,col;
        row=GridLayout.spec(rowcount,1);
        col=GridLayout.spec(colcount,1);
        GridLayout.LayoutParams gridChildparams=new GridLayout.LayoutParams(row,col);
        gridChildparams.width=Cell_Width;
        gridChildparams.height=Cell_Height;
        grid.addView(Items,gridChildparams);
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
    public void ChangeListener(ArrayList<TSListAdaptor.AdaptorDataSet> list) {
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
        for(int i=1;i<list.size()-1;i++){
            String strday=list.get(i).datas[0];
            String strstart=list.get(i).datas[1];
            String strend=list.get(i).datas[2];
            //starttime
            start=Integer.parseInt(strstart);
            if(firststart>start){
                firststart=start;
            }
            //end time
            end=Integer.parseInt(strend);
            if(lastend<end){
                lastend=end;
            }
            day=Integer.parseInt(strday)+1;
            if(lastrecodeday<day){
                lastrecodeday=day;
            }
        }
        if(LegacyStickers!=null){
            for(int i=1;i<LegacyStickers.size()-1;i++){
                String strday=LegacyStickers.get(i).datas[0];
                String strstart=LegacyStickers.get(i).datas[1];
                String strend=LegacyStickers.get(i).datas[2];
                //starttime
                start=Integer.parseInt(strstart);
                if(firststart>start){
                    firststart=start;
                }
                //end time
                end=Integer.parseInt(strend);
                if(lastend<end){
                    lastend=end;
                }
                day=Integer.parseInt(strday)+1;
                if(lastrecodeday<day){
                    lastrecodeday=day;
                }
            }
        }
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


        if(LegacyStickers!=null){
            addSticker(LegacyStickers,firststart,lastend);
        }

        LinkedList<TextView> temp;
        temp=addSticker(list,firststart,lastend);
        for(int i=0;i<temp.size();i++){
            stikerlist.add(temp.get(i));
        }
        UITimeUpdate(firststart/100);
        BaseTableUpdate();
/*        for(int i=0;i<UITable.getChildCount();i++){
            ((TextView)UITable.getChildAt(i)).setBackgroundColor(Color.BLACK);
        }*/
    }
    private LinkedList<TextView> addSticker(ArrayList<TSListAdaptor.AdaptorDataSet> list,int firststart, int lastend){
        int day;
        int start;
        int end;
        Calendar clacbonus=Calendar.getInstance();
        LinkedList<TextView> stickers=new LinkedList<>();
        for(int i=1;i<list.size()-1;i++){
            String strday=list.get(i).datas[0];
            String strstart=list.get(i).datas[1];
            String strend=list.get(i).datas[2];
            String place=list.get(i).datas[3];
            //day
            day=Integer.parseInt(strday)+1;
            //start time
            start=Integer.parseInt(strstart);
            //end time
            end=Integer.parseInt(strend);
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
            print(" cell "+celltime);
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
            stiker.setBackground(getItemsBGWithoutBorder(getItemsRadius(CellStart+cellgridheight,day),Color.argb(150,163,204,163)));
//            print(" Cellstart "+(CellStart+1)+" cellsize "+(cellgridheight)+"");
            GridLayout.LayoutParams params= new GridLayout.LayoutParams(GridLayout.spec(CellStart+1,cellgridheight),GridLayout.spec(day,1));
            params.width=Cell_Width;
            params.height=(int)Customcellsize;
//            print("margin "+(((start%100)/TIME_INTERVAL)*cellbasic));
            if(startminute!=0){
                params.setMargins(0,(int)((startminute/TIME_INTERVAL)*cellbasic),0,0);
            }
            stiker.setLayoutParams(params);
            stiker.setText(place);
            UITable.addView(stiker,UITable.getChildCount());
            stickers.add(stiker);
        }

        return stickers;
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
            }else{
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
    }
    private int getCell_Width(){
        Cell_Width=(TableWidth/TableColCount);
        return Cell_Width;
    }
}
