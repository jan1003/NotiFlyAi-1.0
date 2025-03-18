import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);


        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

       // View rootView = findViewById(R.id.main);
       // if (rootView != null) {
            //ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
               // WindowInsetsCompat systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
               // v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
               // return insets;
          //  });
       // }
    }
}