package com.example.dsl;

import static com.example.dsl.DSLUtil.print;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ts_add extends AppCompatActivity implements View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener, TSListAdaptor.TSListener {
    RecyclerView rv;
    TSListAdaptor ta;
    int TableWidth;
    int TableHeight;
    int Cell_Height=0;
    int Cell_Width=0;
    int TableColCount;
    int TableRowCount;
    GridLayout BaseTable;
    GridLayout UITable;
    LinearLayout BaseTablePosition;
    LinearLayout UITablePosition;
    int TIME_INTERVAL=5;
    final int DEFALUT_COL=6;
    final int DEFALUT_ROW=11;
    int colstart =0;
    List<TextView> stikerlist = new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ts_add);
        Cell_Height=-1;

        init(9);
        //test();
    }
    public void test(){
        //todo
        // 스티커 완성 후 스케쥴에 값 전해주기
        setResult(Activity.RESULT_OK,new Intent());
        finish();

    }
    public void init(int timestart){
        findViewById(R.id.add_cancel).setOnClickListener(this);
        findViewById(R.id.add_accept).setOnClickListener(this);
        UITablePosition=findViewById(R.id.ts_add_ui_table);
        BaseTablePosition=findViewById(R.id.ts_add_base_table);
        Recyclerinit(timestart);

        addLayout(11,6,timestart);
    }
    public void addLayout(int rowcount,int colcount,int timestart){
        this.colstart =timestart;
        TableRowCount=rowcount;
        TableColCount=colcount;

        BaseTable=CreateGridLayout(TableColCount,TableRowCount);
        BaseTablePosition.addView(BaseTable);

        UITable=CreateGridLayout(TableColCount,TableRowCount);
        UITablePosition.addView(UITable);
        UITable.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }
    public void Recyclerinit(int timestart){
        rv=findViewById(R.id.time_setting);
        LinearLayoutManager lm=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rv.setLayoutManager(lm);
        ArrayList<TSListAdaptor.AdaptorDataSet> datalist=new ArrayList<>();
        ta=new TSListAdaptor(this,"월요일",timestart,timestart+1,this);
        rv.setAdapter(ta);
        rv.setHasFixedSize(true);
    }
    private GridLayout CreateGridLayout(int tablecol, int tablerow){
        GridLayout grid=new GridLayout(this);
        grid.setColumnCount(tablecol);
        grid.setRowCount(tablerow);
        GridLayout.LayoutParams gridParam=new GridLayout.LayoutParams();
        gridParam.height=GridLayout.LayoutParams.MATCH_PARENT;
        gridParam.width=GridLayout.LayoutParams.MATCH_PARENT;
        gridParam.setMargins(0,0,0,0);
        grid.setLayoutParams(gridParam);
        grid.setOrientation(GridLayout.VERTICAL);
        grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        return grid;
    }
    //fixme 시간을 늘릴때마다 UI도 같이 늘어남
    private void initUILayout(GridLayout grid){
        TextView v;
        int time;
        for(int rowc=0;rowc<TableRowCount;rowc++){
            for(int colc=0;colc<TableColCount;colc++){
                v=new TextView(this);
                v.setBackground(getItemsBGWithoutBorder(getItemsRadius(rowc,colc)));
                GridLayout.LayoutParams params= getItemLayoutParams(rowc,colc);
                params.setGravity(Gravity.CENTER);
                if(rowc==0 && colc>0){
                    v.setText(ta.dayarray[colc-1]);
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
                v=new View(this);
                v.setBackground(getItemsBGWithBorder(getItemsRadius(rowc,colc)));
                additems(v, grid, rowc, colc);
            }
        }
    }
    public GridLayout.LayoutParams getItemLayoutParams(int rowindex, int colindex){
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
    public float DPtoPX(float dipValue) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    public GradientDrawable getItemsBGWithBorder(float[] radius) {
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setShape(GradientDrawable.RECTANGLE);
        gdDefault.setStroke((int)DPtoPX(1), Color.BLACK);
        gdDefault.setCornerRadii(radius);
        return gdDefault;
    }
    public GradientDrawable getItemsBGWithoutBorder(float[] radius){
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setShape(GradientDrawable.RECTANGLE);
        gdDefault.setCornerRadii(radius);
        return gdDefault;
    }
    public GradientDrawable getItemsBGWithoutBorder(float[] radius,int color){
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setShape(GradientDrawable.RECTANGLE);
        gdDefault.setCornerRadii(radius);
        gdDefault.setColor(color);
        return gdDefault;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_cancel:
                finish();
                break;
            case R.id.add_accept:
                finish();
                break;
        }
    }

    @Override
    public void onGlobalLayout() {
        //if trigger not remove, it is called View any change
        BaseTable.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        TableHeight=BaseTable.getHeight();
        TableWidth=BaseTable.getWidth();
        if(Cell_Width==0){
            Cell_Width=getCell_Width();;
        }
        if(Cell_Height==0){
            Cell_Height=TableHeight/DEFALUT_ROW;
        }else if(Cell_Height==-1){
            Cell_Height=TableHeight/4;
        }
        initBaseLayout(BaseTable);
        initUILayout(UITable);
    }

    @Override
    public void ChangeListener(ArrayList<TSListAdaptor.AdaptorDataSet> list) {

        //첫번째와 마지막은 시간과 관련 없음
        Clear();
        int lastrecodeday=-1;
        int firststart= colstart *100;
        int lastend=(DEFALUT_ROW-1+ colstart)*100;
        int day;
        int start;
        int end;
        for(int i=1;i<list.size()-1;i++){
            String strstart=list.get(i).datas[1];
            String strend=list.get(i).datas[2];
            if(strstart!=null){
                start=Integer.parseInt(strstart);
            }else{
                start= colstart *100;
            }
            if(firststart>start){
                firststart=start;
            }


            //end time
            if(strend!=null){
                end=Integer.parseInt(strend);
            }else{
                end=start+100;
            }
            if(lastend<end){
                lastend=end;
            }
        }
        for(int i=1;i<list.size()-1;i++){
            String strday=list.get(i).datas[0];
            String strstart=list.get(i).datas[1];
            String strend=list.get(i).datas[2];
            String place=list.get(i).datas[3];

            //day
            if(strday!=null){
                day=Integer.parseInt(strday)+1;
            }else{
                day=1;
            }
            if(lastrecodeday<day){
                lastrecodeday=day;
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



            //start time
            if(strstart!=null){
                start=Integer.parseInt(strstart);
            }else{
                start= colstart *100;
            }


            //end time
            if(strend!=null){
                end=Integer.parseInt(strend);
            }else{
                end=start+100;
            }
            int cellbasic=(Cell_Height/((60/TIME_INTERVAL)-1));
            int cell=end-start;
            int cellminute=cell%100;
            int cellhour=cell/100;
            int cellsize=((cellbasic*((cellminute/TIME_INTERVAL)))+(cellhour*Cell_Height));
            //시간 0부터 시작하여 1시간 오를때마다 1씩 증가
            int CellStart=0;


            print("start : "+start+"fiststart : "+firststart+" Cell start : "+(start-firststart)/100);
            if(firststart<900){
/*                int limit=DEFALUT_ROW+(colstart -(start/100))-TableRowCount;
                if(limit>0){
                    for(int i2=0;i2<limit;i2++){
                        addRowBaseLayout();
                    }
                }
                else if(limit<0){
                    for(int i2=limit;i2<0;i2++){
                        removeRowBaseLayout();
                    }
                }*/
                //todo 행 추가로 인한 시간 추가 기능 구현 할 것
                //todo 행 삭제 시 sticker있는지 확인
            }else if(firststart>=900 &&firststart<=1800 && lastend>=900&&lastend<=1900){
/*                print("middle");
                print(firststart);
                if(TableRowCount>DEFALUT_ROW){
                    for(int i2=0, limit=TableRowCount-DEFALUT_ROW;i2<limit;i2++){
                        removeRowBaseLayout();
                    }
                }*/
            }else if(lastend>1900){

            }
            int needcol=(((lastend/100))-(firststart/100));
            int limit=needcol-(TableRowCount-1);
            print("limit:"+limit+" lastend "+lastend+" need col "+needcol);
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
            TextView stiker=new TextView(this);
            stiker.setBackground(getItemsBGWithoutBorder(getItemsRadius(CellStart+cellhour,day),Color.rgb(163,204,163)));

            GridLayout.LayoutParams params= new GridLayout.LayoutParams(GridLayout.spec(CellStart+1,cellhour),GridLayout.spec(day,1));
            params.width=Cell_Width-2;
            params.height=cellsize-2;
            stiker.setLayoutParams(params);
            stiker.setText(list.get(i).datas[3]);
            UITable.addView(stiker,UITable.getChildCount());
            stikerlist.add(stiker);



        }
        //todo sticker 값 담기


        UITimeUpdate(firststart/100);
        BaseTableUpdate();
    }

    private void UITimeUpdate(int starttime){
        for(int i2=TableColCount-1;i2<TableRowCount+TableColCount-2;i2++){
            ((TextView)UITable.getChildAt(i2)).setText((i2-TableColCount+1)+starttime+"");
        }
    }
    private void removeColBaseLayout(){
        for(int i=TableRowCount;i>0;i--){
            BaseTable.removeViewAt((TableColCount*(i))-1);
        }
        UITable.removeViewAt(TableColCount-1);
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
//        Cell_Width=getCell_Width();
    }

    private void addRowBaseLayout(){

        TableRowCount+=1;
        UITable.setRowCount(TableRowCount);
        BaseTable.setRowCount(TableRowCount);
        for(int i=0;i<TableColCount;i++){
            View e=new View(this);
            BaseTable.addView(e,((TableRowCount-1)*TableColCount)+i,getItemLayoutParams(TableRowCount-1,i));
        }

        TextView tv=new TextView(this);
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
            View e=new View(this);
            BaseTable.addView(e,(TableColCount*(i+1))-1,getItemLayoutParams(i,TableColCount-1));

        }

        //UITABLE에 textview 새로 만들시 column추가
        TextView tv=new TextView(this);
        tv.setText(ta.dayarray[TableColCount-2]);
        GridLayout.LayoutParams params=getItemLayoutParams(0,TableColCount-1);
        params.setGravity(Gravity.CENTER);
        tv.setWidth(Cell_Width);
        tv.setGravity(Gravity.CENTER);
        UITable.addView(tv,TableColCount-1,params);
    }

    public void BaseTableUpdate(){
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
    public void UITableReset(){
        for(int i=0;i<UITable.getChildCount();i++){
            GridLayout.LayoutParams ee=(GridLayout.LayoutParams)UITable.getChildAt(i).getLayoutParams();
            ee.width=getCell_Width();
            //ss;
            UITable.getChildAt(i).setLayoutParams(ee);
        }
    }

    public void Clear(){
        for(int i = 0; i< stikerlist.size(); i++){
            UITable.removeView(stikerlist.get(i));
        }
    }
    private int getCell_Width(){
        Cell_Width=(TableWidth/TableColCount);
        return Cell_Width;
    }
}