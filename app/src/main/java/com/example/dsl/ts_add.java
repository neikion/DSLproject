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
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ts_add extends AppCompatActivity implements View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener, TSListAdaptor.TSListener {
    RecyclerView rv;
    TSListAdaptor ta;
    int TableWidth;
    int TableHeight;
    int Cell_Height=0;
    int Cell_Width=0;
    int TableColCount;
    int TableRowCount;
    float RowCellDivdeNum=5f;
    GridLayout BaseTable;
    GridLayout UITable;
    LinearLayout BaseTablePosition;
    LinearLayout UITablePosition;
    int TIME_INTERVAL=5;
    final int DEFALUT_COL=11;
    final int DEFALUT_ROW=6;
    int colstart =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ts_add);
        Cell_Height=-1;

        init();
        //test();
    }
    public void test(){
        int getid=getIntent().getIntExtra("BaseTableID",0);
        //((TextView)UITable.getChildAt(0)).setText("ddd");
        setResult(Activity.RESULT_OK,new Intent());
        finish();

    }
    public void init(){
        findViewById(R.id.add_cancel).setOnClickListener(this);
        findViewById(R.id.add_accept).setOnClickListener(this);
        UITablePosition=findViewById(R.id.ts_add_ui_table);
        BaseTablePosition=findViewById(R.id.ts_add_base_table);
        Recyclerinit();

        addLayout(11,6,9);
    }
    public void addLayout(int rowcount,int colcount,int colstart){
        this.colstart =colstart;
        TableRowCount=rowcount;
        TableColCount=colcount;

        BaseTable=CreateGridLayout(TableColCount,TableRowCount);
        BaseTablePosition.addView(BaseTable);

        UITable=CreateGridLayout(TableColCount,TableRowCount);
        UITablePosition.addView(UITable);
        UITable.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }
    public void Recyclerinit(){
        rv=findViewById(R.id.time_setting);
        LinearLayoutManager lm=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        rv.setLayoutManager(lm);
        ArrayList<TSListAdaptor.AdaptorDataSet> datalist=new ArrayList<>();
        ta=new TSListAdaptor(this,"월요일","09:00","10:00",this);
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
        return grid;
    }
    private void UILayoutSetting(GridLayout grid){
        TextView v;
        for(int rowc=0;rowc<TableRowCount;rowc++){
            for(int colc=0;colc<TableColCount;colc++){
                v=new TextView(this);
                v.setPadding((int)DPtoPX(10),(int)DPtoPX(0),5,5);
                if(rowc==0 && colc>0){
                    v.setText(ta.dayarray[colc-1]);
                }
/*                if(rowc>0&&colc==0){
                    v.setText(rowc+8+"");
                }*/
                //test
                int time=(rowc+(colstart-1))%12;
                if(time==0){
                    time=12;
                }
                if(rowc>0&&colc==0){
                    v.setText(time+"");
                }
                v.setBackground(getItemsBGWithoutBorder(getItemsRadius(rowc,colc)));
                ItemsAdd(v, grid, rowc, colc);
            }

        }
    }
    private void BaseLayoutSetting(GridLayout grid){
        View v;
        //cell setting init
        for(int rowc=0;rowc<TableRowCount;rowc++){
            for(int colc=0;colc<TableColCount;colc++){
                v=new View(this);
                v.setBackground(getItemsBGWithBorder(getItemsRadius(rowc,colc)));
                ItemsAdd(v, grid, rowc, colc);

            }
        }
    }
    private void ItemsAdd(View Items,GridLayout grid,int rowcount,int colcount){
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
            Cell_Width=TableWidth/TableColCount;
        }
        if(Cell_Height==0){
            Cell_Height=TableHeight/TableRowCount;
        }else if(Cell_Height==-1){
//            Cell_Height=(int)DPtoPX(75);
            Cell_Height=(int)DPtoPX(50);
        }
        BaseLayoutSetting(BaseTable);
        UILayoutSetting(UITable);
    }

    @Override
    public void ChangeListener(ArrayList<TSListAdaptor.AdaptorDataSet> list) {

        //첫번째와 마지막은 시간과 관련 없음
        Clear();
        for(int i=1;i<list.size()-1;i++){
            String strday=list.get(i).datas[0];
            String strstart=list.get(i).datas[1];
            String strend=list.get(i).datas[2];
            int day;
            int start;
            int end;
            String place=list.get(i).datas[3];
            if(strday!=null){
                day=Integer.parseInt(strday)+1;
            }else{
                day=1;
            }
            if(strstart!=null){
                start=Integer.parseInt(strstart);
            }else{
                start=900;
            }
            if(strend!=null){
                end=Integer.parseInt(strend);
            }else{
                end=1000;
            }

            if(start<900){

/*                new Handler().postDelayed((int col,int i1)->{
                    int bonusday=TableColCount*day+1;
                    int cellbasic=(Cell_Height/((60/TIME_INTERVAL)-1));
                    int cell=(end-start);
                    int cellminute=cell%100;
                    int cellhour=cell/100;
                    int cellsize=((cellbasic*((cellminute/TIME_INTERVAL)))+(cellhour*Cell_Height));
                    int CellStart=((start-col)/100);
                    ((TextView)UITable.getChildAt(CellStart+bonusday)).setBackground(getItemsBGWithoutBorder(getItemsRadius(CellStart+cellhour,day),Color.rgb(163,204,163)));
                    GridLayout.LayoutParams edit=new GridLayout.LayoutParams(GridLayout.spec(CellStart+1,cellhour),GridLayout.spec(day,1));
                    edit.height=cellsize;
                    edit.width=Cell_Width;
                    UITable.getChildAt(CellStart+bonusday).setLayoutParams(edit);
                    ((TextView) UITable.getChildAt(CellStart+bonusday)).setText(list.get(i1).datas[3]);
                    v.add((TextView)UITable.getChildAt(CellStart+bonusday));},3000);*/

            }else if(start>=900 && start<=1800){
/*                int bonusday=TableColCount*day+1;
                int cellbasic=(Cell_Height/((60/TIME_INTERVAL)-1));
                int cell=(end-start);
                int cellminute=cell%100;
                int cellhour=cell/100;
                int cellsize=((cellbasic*((cellminute/TIME_INTERVAL)))+(cellhour*Cell_Height));
                int CellStart=((start-900)/100);
                TableRowCount+=1;
                UITable.setRowCount(TableRowCount);
                TableColCount+=1;
                UITable.setColumnCount(TableColCount);

                TextView e=new TextView(this);
                e.setText("test");
                GridLayout.Spec row,col;
                row=GridLayout.spec(11,1);
                col=GridLayout.spec(6,1);
                GridLayout.LayoutParams gridChildparams=new GridLayout.LayoutParams(row,col);
                gridChildparams.width=Cell_Width;
                gridChildparams.height=Cell_Height;
                UITable.addView(e,gridChildparams);
                Cell_Width=TableWidth/TableColCount;
                for(int i2=0;i2<BaseTable.getChildCount();i2++){
                    GridLayout.LayoutParams ee=(GridLayout.LayoutParams)BaseTable.getChildAt(i2).getLayoutParams();
                    ee.width=Cell_Width;
                    BaseTable.getChildAt(i2).setLayoutParams(ee);
                }*/
                //addLayout(25,6,9);
/*                new Handler().postDelayed(()->{
                    for(int i1=1;i1<TableRowCount;i1++){

                        ((TextView) UITable.getChildAt(TableColCount*(i1))).setText(time+"");
                    }

                },1000);*/

                /*((TextView)UITable.getChildAt(CellStart+bonusday)).setBackground(getItemsBGWithoutBorder(getItemsRadius(CellStart+cellhour,day),Color.rgb(163,204,163)));
                GridLayout.LayoutParams edit=new GridLayout.LayoutParams(GridLayout.spec(CellStart+1,cellhour),GridLayout.spec(day,1));
                edit.height=cellsize;
                edit.width=Cell_Width;
                UITable.getChildAt(CellStart+bonusday).setLayoutParams(edit);
                ((TextView) UITable.getChildAt(CellStart+bonusday)).setText(list.get(i).datas[3]);
                v.add((TextView)UITable.getChildAt(CellStart+bonusday));*/
                /*
                int bonusday=TableColCount*day+1;
                    int cellbasic=(Cell_Height/((60/TIME_INTERVAL)-1));
                    int cell=(end-start);
                    int cellminute=cell%100;
                    int cellhour=cell/100;
                    int cellsize=((cellbasic*((cellminute/TIME_INTERVAL)))+(cellhour*Cell_Height));
                    int CellStart=((start-col)/100);
                    ((TextView)UITable.getChildAt(CellStart+bonusday)).setBackground(getItemsBGWithoutBorder(getItemsRadius(CellStart+cellhour,day),Color.rgb(163,204,163)));
                    GridLayout.LayoutParams edit=new GridLayout.LayoutParams(GridLayout.spec(CellStart+1,cellhour),GridLayout.spec(day,1));
                    edit.height=cellsize;
                    edit.width=Cell_Width;
                    UITable.getChildAt(CellStart+bonusday).setLayoutParams(edit);
                    ((TextView) UITable.getChildAt(CellStart+bonusday)).setText(list.get(i1).datas[3]);
                    v.add((TextView)UITable.getChildAt(CellStart+bonusday));*/
            }else if(start>1800){

            }




        }
    }
    List<TextView> v= new LinkedList<>();
    public void Clear(){
        for(int i=0;i<v.size();i++){
            v.get(i).setBackground(null);
            v.get(i).setText("");
            GridLayout.LayoutParams edit=new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED,1),GridLayout.spec(GridLayout.UNDEFINED,1));
            edit.height=Cell_Height;
            edit.width=Cell_Width;
            v.get(i).setLayoutParams(edit);
        }
    }
}