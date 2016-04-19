package biz.antworks.locationupdater;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class CustomList extends ArrayAdapter<String> {


    private final Activity context;
    private String[] Start;
    private String[] Dest;
    private String[] Date;
    private String[] Status;
    TextView txtTitle1;
    TextView txtTitle2;
    TextView txtTitle3;
    TextView txtTitle4;

   protected CustomList(Activity context,
                      String[] Date, String[] Start, String[] Dest , String[] Status) {
        super(context, R.layout.listviewexample, Start);
        this.context = context;

        this.Date=Date;
        this.Start= Start;
        this.Dest=Dest;
        this.Status=Status;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.listviewexample, null, true);
        txtTitle1 = (TextView) rowView.findViewById(R.id.Date);
        txtTitle2 = (TextView) rowView.findViewById(R.id.Start);
        txtTitle3 = (TextView) rowView.findViewById(R.id.Dest);
        txtTitle4 = (TextView) rowView.findViewById(R.id.Status);

        txtTitle1.setText(Date[position]);
        txtTitle2.setText(Start[position]);
        txtTitle3.setText(Dest[position]);
        txtTitle4.setText(Status[position]);

        return rowView;
    }
}
