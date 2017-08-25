package com.example.zhenqiangli.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.example.zhenqiangli.customview.charting.PieChart;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final PieChart pie = (PieChart) this.findViewById(R.id.Pie);
    pie.addItem("Agamemnon", 2, getResources().getColor(R.color.seafoam));
    pie.addItem("Bocephus", 3.5f, getResources().getColor(R.color.chartreuse));
    pie.addItem("Calliope", 2.5f, getResources().getColor(R.color.emerald));
    pie.addItem("Daedalus", 3, getResources().getColor(R.color.bluegrass));
    pie.addItem("Euripides", 1, getResources().getColor(R.color.turquoise));
    pie.addItem("Ganymede", 3, getResources().getColor(R.color.slate));

    findViewById(R.id.Reset).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        pie.setCurrentItem(0);
      }
    });
  }
}
