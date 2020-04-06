package com.sisixa.dtt_application;

import android.app.AppComponentFactory;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create the button on the main activity for mobile mode
        Button theButton = (Button)findViewById(R.id.btnOne);
         theButton.setBackgroundResource(R.drawable.main_btn_bg);


        theButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);

                MainActivity.this.startActivity(myIntent);
            }
        });





        //create my tablet layout an if it isn't null, it means that my app run in tabletand then i create the two buttons
        View rightPanel = findViewById(R.id.maintabetlayout);
        if (rightPanel != null) {




            Button theButtonTblet = (Button)findViewById(R.id.btnOne);
            theButtonTblet.setBackgroundResource(R.drawable.main_btn_bg);



            Button theButtonTbletTwo = (Button)findViewById(R.id.btnPrivacy);
            theButtonTbletTwo.setBackgroundResource(R.drawable.main_btn_bg);


            theButtonTblet.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);

                    MainActivity.this.startActivity(myIntent);
                }
            });


            theButtonTbletTwo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    myMethod();
                }
            });




            }



        }







        @Override
        public boolean onCreateOptionsMenu (Menu menu){

            //if my app runs in mobile then i make the "info button"

            View rightPanel = findViewById(R.id.maintabetlayout);
            if (rightPanel == null) {


                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.main_menu, menu);

                // show the button when some condition is true
            }



            return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.



            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_name) {


                myMethod();
                return true;

            }


            return super.onOptionsItemSelected(item);

    }



    //myMethod makes the messege about    privacybeleid
    public void myMethod(){
        String link1 = "<a href=\"https://www.rsr.nl/index.php?page=privacy-wetgeving\">privacybeleid</a>";
        String message = "Om deze app te gebruiken, dient u het "+link1+" te accepteren.";
        Spanned myMessage = Html.fromHtml(message);


        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
        builder.setTitle("Privacybeleid");
        builder.setMessage(myMessage);
        builder.setCancelable(true);


        //make the BEVESTIG button
        builder.setPositiveButton("BEVESTIG", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView msgTxt = (TextView) alertDialog.findViewById(android.R.id.message);
        msgTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }






}
