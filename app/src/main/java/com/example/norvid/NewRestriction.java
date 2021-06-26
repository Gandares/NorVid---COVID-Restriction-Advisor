package com.example.norvid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewRestriction extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "DocSnippets";
    int lid = 3000;

    ArrayList<Integer> ids = new ArrayList<>();

    EditText documentN;

    Button save, exit, deleteR, deleteLines, addNewLine;

    ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_restriction);

        String emailUse = getIntent().getStringExtra("email");

        cl = findViewById(R.id.clnewLines);

        documentN = findViewById(R.id.documentN);

        save = findViewById(R.id.gys);
        exit = findViewById(R.id.salir);
        deleteR = findViewById(R.id.borrar);
        deleteLines = findViewById(R.id.borrarLineas);
        addNewLine = cl.findViewById(R.id.agregar);

        deleteR.setOnClickListener(v -> {
            setButtons(false);
            if(!documentN.getText().toString().equals("")&&documentN.getText()!=null){
                db.collection("Listas").document("Restricciones").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ArrayList listaRestricciones = (ArrayList) document.getData().get("r");
                                boolean match = listaRestricciones.contains(documentN.getText().toString());
                                if(match){
                                    listaRestricciones.remove(documentN.getText().toString());
                                    Map<String, Object> aux = new HashMap<>();
                                    aux.put("r", listaRestricciones);
                                    db.collection("Listas").document("Restricciones").set(aux)
                                        .addOnSuccessListener(aVoid -> {
                                            db.collection("Restricciones").document(documentN.getText().toString()).delete()
                                                .addOnSuccessListener(aVoid1 -> {
                                                    AlertDialog.Builder error = new AlertDialog.Builder(NewRestriction.this);
                                                    error.setMessage("Restricción " + documentN.getText().toString() + " borrada" );
                                                    error.setPositiveButton("Aceptar", null);

                                                    AlertDialog dialog = error.create();
                                                    dialog.show();
                                                });
                                        });
                                }
                                else{
                                    AlertDialog.Builder error = new AlertDialog.Builder(NewRestriction.this);
                                    error.setTitle("Error");
                                    error.setMessage("La restricción introducida no existe.");
                                    error.setPositiveButton("Aceptar", null);

                                    AlertDialog dialog = error.create();
                                    dialog.show();
                                }
                            }
                        }
                    });
            }
            else{
                AlertDialog.Builder error = new AlertDialog.Builder(NewRestriction.this);
                error.setTitle("Error");
                error.setMessage("Cadena de texto vacía.");
                error.setPositiveButton("Aceptar", null);

                AlertDialog dialog = error.create();
                dialog.show();
            }
            setButtons(true);
        });

        addNewLine.setOnClickListener(v -> {
            String[] options = new String[]{"Mostrar texto", "Insertar texto"};
            AlertDialog.Builder opciones = new AlertDialog.Builder(NewRestriction.this);
            opciones.setTitle("Añada un campo a la restricción");
            opciones.setSingleChoiceItems(options, -1, (dialog, which) -> {
                if(options[which].equals("Mostrar texto")){
                    insertLine(cl,1, addNewLine);
                    dialog.dismiss();
                }
                else if(options[which].equals("Insertar texto")){
                    insertLine(cl,2, addNewLine);
                    dialog.dismiss();
                }
            });
            opciones.setNeutralButton("Cancel", (dialog, which) -> { });
            AlertDialog dialog = opciones.create();
            dialog.show();
        });

        deleteLines.setOnClickListener(v -> {
            deleteAll();
        });

        save.setOnClickListener(v -> {
            setButtons(false);
            if(!documentN.getText().toString().equals("")&&documentN.getText()!=null){
                if(ids.size()>0){
                    Map<String, Object> nuevaR = new HashMap<>();

                    for(int i = 0; i < ids.size(); i++){
                        if(cl.findViewById(ids.get(i)).getClass().getName().equals("android.widget.TextView")){
                            String key = "dato" + i;
                            nuevaR.put(key, "");
                        }
                        else if(cl.findViewById(ids.get(i)).getClass().getName().equals("android.widget.EditText")){
                            EditText et = findViewById(ids.get(i));
                            nuevaR.put(Integer.toString(i), et.getText().toString());
                        }
                    }
                    db.collection("Listas").document("Restricciones").get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    ArrayList listaRestricciones = (ArrayList) document.getData().get("r");
                                    boolean match = listaRestricciones.contains(documentN.getText().toString());
                                    if (match) {
                                        AlertDialog.Builder error = new AlertDialog.Builder(NewRestriction.this);
                                        error.setTitle("Error");
                                        error.setMessage("Nombre de restricción ya existente.");
                                        error.setPositiveButton("Aceptar", null);
                                        AlertDialog dialog = error.create();
                                        dialog.show();
                                    }
                                    else{
                                        listaRestricciones.add(documentN.getText().toString());
                                        Map<String, Object> aux = new HashMap<>();
                                        aux.put("r", listaRestricciones);
                                        db.collection("Listas").document("Restricciones").set(aux)
                                            .addOnSuccessListener(aVoid -> {
                                                db.collection("Restricciones").document(documentN.getText().toString()).set(nuevaR)
                                                    .addOnSuccessListener(aVoid2 -> {
                                                        AlertDialog.Builder error = new AlertDialog.Builder(NewRestriction.this);
                                                        error.setTitle("Completo");
                                                        error.setMessage("Se creó la restricción " + documentN.getText().toString() + " correctamente.");
                                                        error.setPositiveButton("Aceptar", null);
                                                        AlertDialog dialog = error.create();
                                                        dialog.show();

                                                        deleteAll();
                                                    });
                                            });
                                    }
                                }
                            }
                        });
                }
                else{
                    AlertDialog.Builder error = new AlertDialog.Builder(NewRestriction.this);
                    error.setTitle("Error");
                    error.setMessage("Restricción vacía.");
                    error.setPositiveButton("Aceptar", null);

                    AlertDialog dialog = error.create();
                    dialog.show();
                }
            }
            else{
                AlertDialog.Builder error = new AlertDialog.Builder(NewRestriction.this);
                error.setTitle("Error");
                error.setMessage("Ningún nombre añadido a la restricción.");
                error.setPositiveButton("Aceptar", null);

                AlertDialog dialog = error.create();
                dialog.show();
            }
            setButtons(true);
        });

        exit.setOnClickListener(v -> {
            Intent insertIntent = new Intent(NewRestriction.this, InsertRestrictions.class).putExtra("email", emailUse);
            NewRestriction.this.startActivity(insertIntent);
        });

    }

    private void insertLine(ConstraintLayout cl, int option, Button itself){
        ConstraintSet set = new ConstraintSet();
        if(option == 1){
            EditText view = new EditText(NewRestriction.this);
            view.setId(lid);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            view.setTextSize(17);
            view.setTextColor(Color.parseColor("#BBBBBB"));
            cl.addView(view, lp);
            set.clone(cl);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) itself.getLayoutParams();
            set.connect(view.getId(), ConstraintSet.TOP, params.topToBottom, ConstraintSet.BOTTOM,40);
            set.connect(view.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 30);
            set.connect(itself.getId(), ConstraintSet.TOP, view.getId(), ConstraintSet.BOTTOM, 40);
        }
        else if(option == 2){
            TextView view = new TextView(NewRestriction.this);
            view.setId(lid);
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            view.setText("Campo para texto");
            view.setTextSize(17);
            view.setTextColor(Color.parseColor("#BBBBBB"));
            cl.addView(view, lp);
            set.clone(cl);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) itself.getLayoutParams();
            set.connect(view.getId(), ConstraintSet.TOP, params.topToBottom, ConstraintSet.BOTTOM,40);
            set.connect(view.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 30);
            set.connect(itself.getId(), ConstraintSet.TOP, view.getId(), ConstraintSet.BOTTOM, 40);
        }
        set.applyTo(cl);
        ids.add(lid);
        lid++;
    }

    private void deleteAll(){
        for(int i = 0; i < ids.size(); i++)
            cl.removeView(cl.findViewById(ids.get(i)));
        ids.clear();
        ConstraintSet set = new ConstraintSet();
        set.clone(cl);
        set.connect(addNewLine.getId(),ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 40);
        set.applyTo(cl);
    }


    private void setButtons(boolean tof){
        deleteR.setEnabled(tof);
        save.setEnabled(tof);
        exit.setEnabled(tof);
        deleteLines.setEnabled(tof);
        addNewLine.setEnabled(tof);
    }
}