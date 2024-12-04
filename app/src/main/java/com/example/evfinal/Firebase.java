package com.example.evfinal;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Firebase extends AppCompatActivity {

    EditText txtCodigo, txtNombre, txtDueño, txtDireccion;
    Spinner spMascota;
    Button btnEnviar, btnCargar;
    ListView lista;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mascotasRef = db.collection("mascotas");

    List<String> mascotas = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase); // Cambia a activity_firebase

        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        spMascota = findViewById(R.id.spMascota);
        txtDueño = findViewById(R.id.txtDueño);
        txtDireccion = findViewById(R.id.txtDireccion);
        btnEnviar = findViewById(R.id.btnEnviarr);
        btnCargar = findViewById(R.id.btnCargarr);
        lista = findViewById(R.id.lista);

        // Inicializar el Spinner con las opciones de mascotas
        mascotas.add("Perro");
        mascotas.add("Gato");
        mascotas.add("Conejo");
        // ... agregar más opciones

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mascotas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMascota.setAdapter(adapter);

        // Configurar el botón "Enviar Datos"
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDatosFirestore();
            }
        });

        // Configurar el botón "Cargar Datos"
        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarLista();
            }
        });
    }

    // Método para enviar datos a Firestore
    public void enviarDatosFirestore() {
        String codigo = txtCodigo.getText().toString();
        String nombre = txtNombre.getText().toString();
        String mascota = spMascota.getSelectedItem().toString();
        String dueño = txtDueño.getText().toString();
        String direccion = txtDireccion.getText().toString();

        if (codigo.isEmpty() || nombre.isEmpty() || dueño.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> mascotaData = new HashMap<>();
        mascotaData.put("codigo", codigo);
        mascotaData.put("nombre", nombre);
        mascotaData.put("tipo", mascota);
        mascotaData.put("dueño", dueño);
        mascotaData.put("direccion", direccion);

        mascotasRef.add(mascotaData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Datos enviados correctamente", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al enviar datos", Toast.LENGTH_SHORT).show();
                });
    }

    // Método para cargar la lista de mascotas desde Firestore
    public void CargarLista() {
        mascotasRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> mascotasList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String nombreMascota = document.getString("nombre");
                        mascotasList.add(nombreMascota);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mascotasList);
                    lista.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                });
    }

    // Método para limpiar los campos del formulario
    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        spMascota.setSelection(0); // Seleccionar la primera opción del Spinner
        txtDueño.setText("");
        txtDireccion.setText("");
    }
}