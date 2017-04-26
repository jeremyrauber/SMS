package com.projetos.megabit.testes;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String turno;
    String turma;
    String serie;
    EditText edtText;
    String textoSMS;
    ListView listview ;
    Aluno[] alunos;
    CheckBox cb1;
    Integer permissionBtnClickOnce = 0;
    String msgFinal = "Fim.";

    SparseBooleanArray sparseBooleanArray ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView)findViewById(R.id.listView);
        edtText = (EditText)findViewById(R.id.edt_SMS);

        listview.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                sparseBooleanArray = listview.getCheckedItemPositions();

                int i = 0 ;

                while (i < sparseBooleanArray.size()) {

                    if (sparseBooleanArray.valueAt(i)) {
                        alunos[ sparseBooleanArray.keyAt(i) ].setValue(1);
                    }

                    i++ ;
                }

            }
        });

        Spinner spinnerTurma = (Spinner) findViewById(R.id.turma);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterTurma = ArrayAdapter.createFromResource(this,
                R.array.turma, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTurma.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerTurma.setAdapter(adapterTurma);

        Spinner spinnerSerie = (Spinner) findViewById(R.id.serie);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterSerie = ArrayAdapter.createFromResource(this,
                R.array.series, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterSerie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerSerie.setAdapter(adapterSerie);

        Spinner spinnerTurno = (Spinner) findViewById(R.id.turno);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterTurno = ArrayAdapter.createFromResource(this,
                R.array.turno, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterTurno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerTurno.setAdapter(adapterTurno);


        spinnerSerie.setOnItemSelectedListener(this);
        spinnerTurma.setOnItemSelectedListener(this);
        spinnerTurno.setOnItemSelectedListener(this);


        Button btnClicar = (Button) findViewById(R.id.btnbuscar);
        btnClicar.setOnClickListener(new View.OnClickListener(){


            public void onClick(View v)
            {
                ChamadaWeb chamada = new ChamadaWeb("http://megabit1.dyndns.org/projetoEscola/webservice.php",turma,turno,serie);
                chamada.execute();

            }
        });

        cb1 = (CheckBox) findViewById(R.id.cb1);
        cb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb1.isChecked()){
                    System.out.println("Checked");
                    for(int i = 0; i<listview.getCount(); i++){
                        listview.setItemChecked(i, true);
                        alunos[i].setValue(1);
                    }
                }else{
                    System.out.println("Un-Checked");
                    for(int i = 0; i<listview.getCount(); i++){
                        listview.setItemChecked(i, false);
                        alunos[i].setValue(0);
                    }
                }
            }
        });


        Button btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                textoSMS = edtText.getText().toString();
                int numeroAlunosInativos = 0;
                String nomeAlunosNaoMatriculados = "";
                int deuBom = 0;

                if (alunos != null && !textoSMS.equals("")) {
                    for (final Aluno al : alunos) {
                        System.out.println(al.getAtivo());
                        if (al.getValue() == 1 && al.getAtivo() == 1) {
                            deuBom = 1;
                            msgFinal = sendSMS(al.getCelular(), textoSMS);

                        } else if (al.getValue() == 1 && al.getAtivo() == 0) {
                            nomeAlunosNaoMatriculados += al.getNome() + ", ";
                            numeroAlunosInativos++;
                        }
                    }
                } else {
                    msgFinal = "Preencha o campo da mensagem!";
                }
                if (deuBom == 1)
                    showToast("Numero de alunos inativos que não receberam sms: "+numeroAlunosInativos+" !",1);
                showToast(msgFinal,0);
                ChamadaWeb2 chamada2 = new ChamadaWeb2("http://megabit1.dyndns.org/projetoEscola/webserviceEnvio.php");
                chamada2.execute();

            }
        });
    }

    public void showToast(final String toast, final int duracao)
    {
        runOnUiThread(new Runnable() {
            public void run()
            {
                if(duracao==1)
                    Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String sendSMS(String phoneNumber, String message) {
       try {
           SmsManager sms = SmsManager.getDefault();
           sms.sendTextMessage(phoneNumber, null, message, null, null);
           return "Mensagens enviadas com sucesso!";
       }catch (Exception e){
           return "Erro ao enviar mensagem";
       }
    }


    public void onItemSelected(AdapterView<?> parent, View view,int pos, long id) {

        switch (parent.getId()) {
            case R.id.turno:
                turno= parent.getSelectedItem().toString();
                break;
            case R.id.serie:
                serie= parent.getSelectedItem().toString();
                break;
            case R.id.turma:
                turma = parent.getSelectedItem().toString();

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class ChamadaWeb extends AsyncTask<String, Void, String> {
        private String enderecoWeb;
        private String webSerie;
        private String webTurma;
        private String webTurno;

        public ChamadaWeb(String endereco,String turma, String turno, String serie) {
            enderecoWeb = endereco;
            webSerie = serie;
            webTurno = turno;
            webTurma = turma;
        }


        @Override  // ESTA INDO POR GET
        protected String doInBackground(String... params) {
            HttpClient cliente = HttpClientBuilder.create().build();

            try {
                if (!enderecoWeb.endsWith("?"))
                    enderecoWeb += "?";

                List<NameValuePair> parameters = new LinkedList<NameValuePair>();

                if (!webSerie.isEmpty())
                    parameters.add(new BasicNameValuePair("serie", webSerie));
                if (!webTurno.isEmpty())
                    parameters.add(new BasicNameValuePair("turno", webTurno));
                if (!webTurma.isEmpty())
                    parameters.add(new BasicNameValuePair("turma", webTurma));

                String paramString = URLEncodedUtils.format(parameters, "utf-8");

                enderecoWeb += paramString;

                System.out.println(enderecoWeb);

                HttpGet chamada = new HttpGet(enderecoWeb);
                HttpResponse resposta = cliente.execute(chamada);
                return EntityUtils.toString(resposta.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(String resultado) {
            if (resultado != null) {
                atualizaMensagem(resultado);
            }
        }
    }
    public void atualizaMensagem(String resultado) {

        Gson gson = new Gson();

        alunos = gson.fromJson(resultado, Aluno[].class);

        ArrayAdapter<Aluno> adapter = new ArrayAdapter<Aluno>
                (MainActivity.this,
                        R.layout.row,
                        android.R.id.text1, alunos);

        listview.setAdapter(adapter);


    }

    private class ChamadaWeb2 extends AsyncTask<String, Void, String> {
        private String enderecoWeb;

        public ChamadaWeb2(String endereco) {
            enderecoWeb = endereco;
        }


        @Override  // ESTA INDO POR POST
        protected String doInBackground(String... params) {
            HttpClient cliente = HttpClientBuilder.create().build();

            try {
                HttpPost chamada = new HttpPost(enderecoWeb);
                List<NameValuePair> parametros = new ArrayList<NameValuePair>(1);

                List<Aluno> alunosMensagem = new ArrayList<>();
                for(Aluno a: alunos){
                     if(a.getValue()==1 && a.getAtivo()==1) {
                         alunosMensagem.add(a);
                     }
                }

                for(Aluno a: alunosMensagem){

                       System.out.println(a.getCelular()+a.getNome()+a.getAtivo()+a.getSerie()+a.getNome());
                }

                String jsonString = new Gson().toJson(alunosMensagem);

                List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
                System.out.println(jsonString);

                parameters.add(new BasicNameValuePair("jeison", jsonString));
                parameters.add(new BasicNameValuePair("mensagem", textoSMS));

                chamada.setEntity(new UrlEncodedFormEntity(parameters));

                HttpResponse resposta = cliente.execute(chamada);

                String responseBody = EntityUtils.toString(resposta.getEntity());

                return responseBody;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(String resultado) {

        }
    }

}