package com.wahyuzulya.perpustakaansmanel.peminjam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wahyuzulya.perpustakaansmanel.CustomVolleyRequestQueue;
import com.wahyuzulya.perpustakaansmanel.DbContract;
import com.wahyuzulya.perpustakaansmanel.MainActivity;
import com.wahyuzulya.perpustakaansmanel.R;
import com.wahyuzulya.perpustakaansmanel.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PeminjamPinjam extends AppCompatActivity {
    Toolbar toolbar;
    ArrayList<peminjamObjek>list;
    ListView listView;
    ImageButton ibtn_logout;
    Button btn_riwayat;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peminjam);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.tl_peminjam);
        listView = findViewById(R.id.lv_buku);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        final SessionManager sessionManager = new SessionManager(getApplicationContext());

        ibtn_logout = (ImageButton) findViewById(R.id.ibtn_logout);
        ibtn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Keluar");
                builder.setMessage("Kamu yakin ingin keluar?");
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sessionManager.setLogin(false);
                        sessionManager.setUsername("");
                        startActivity(new Intent(PeminjamPinjam.this,MainActivity.class));
                        finish();
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(),sessionManager.getId(),Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        btn_riwayat = (Button) findViewById(R.id.btn_pinjamRiwayat);
        btn_riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PeminjamPinjam.this,RiwayatPeminjaman1.class));
                finish();
            }
        });
        tampilData();

    }
    void tampilData(){
        list = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_COLLECTDATA_BUKU,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i=0; i<jsonArray.length();i++){
                                JSONObject getData=jsonArray.getJSONObject(i);
                                String kodeBuku = getData.getString("kode");
                                String judul = getData.getString("judul");
                                String subJudul = getData.getString("subjudul");
                                String pengarang = getData.getString("pengarang");
                                String penerbit = getData.getString("penerbit");
                                String tahunTerbit = getData.getString("tahunterbit");
                                String kategori = getData.getString("kategori");
                                String kelas = getData.getString("kelas");
                                String jumlah = getData.getString("jumlah");
                                String img = "https://wahyuzly.000webhostapp.com/page/buku/"+getData.getString("judul")+".jpg";


                                //Toast.makeText(getApplicationContext(),kategori+" "+kelas+" "+jumlah,Toast.LENGTH_LONG).show();
                                list.add(new peminjamObjek(kodeBuku,judul,subJudul,pengarang,penerbit,tahunTerbit,kategori,kelas,jumlah,img));
                                //Toast.makeText(getApplicationContext(),list.toString(),Toast.LENGTH_LONG).show();
                            }
                            Adapter adapter = new Adapter(PeminjamPinjam.this,list);
                            listView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

class Adapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    ArrayList<peminjamObjek> model;
    public Adapter(Context context, ArrayList<peminjamObjek>model){
        inflater=LayoutInflater.from(context);
        this.context=context;
        this.model = model;

    }

    @Override
    public int getCount() {
        return model.size();
    }

    @Override
    public Object getItem(int position) {
        return model.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    TextView kodeBuku,judulBuku,subJudulBuku,pengarang,penerbit,tahunTerbit,kategori,kelas,jumlah;
    NetworkImageView gambar;
    ImageLoader imageLoader;
    Button btnPinjam;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_data_peminjam_buku,parent,false);
        kodeBuku=view.findViewById(R.id.tv_kodeBuku);
        judulBuku=view.findViewById(R.id.tv_judulBuku);
        subJudulBuku=view.findViewById(R.id.tv_subJudulBuku);
        pengarang=view.findViewById(R.id.tv_pengarang);
        penerbit=view.findViewById(R.id.tv_penerbit);
        tahunTerbit=view.findViewById(R.id.tv_tahunTerbit);
        kategori=view.findViewById(R.id.tv_kategori);
        kelas=view.findViewById(R.id.tv_kelas);
        jumlah=view.findViewById(R.id.tv_JumlahBuku);
        gambar=view.findViewById(R.id.niv_gambar);
        btnPinjam = view.findViewById(R.id.btn_pinjam);

        kodeBuku.setText(model.get(position).getKode());
        judulBuku.setText(model.get(position).getJudul());
        subJudulBuku.setText(model.get(position).getSubJudul());
        pengarang.setText("Pengarang\t: "+model.get(position).getPengarang());
        penerbit.setText("Penerbit\t: "+model.get(position).getPenerbit());
        tahunTerbit.setText("Tahun terbit: "+model.get(position).getTahunTerbit());
        kategori.setText("Kategori\t:"+model.get(position).getKategori());
        kelas.setText("Kelas\t: "+model.get(position).getKelas());
        jumlah.setText("Jumlah\t: "+model.get(position).getJumlah());
        String url = model.get(position).getImg();
        imageLoader = CustomVolleyRequestQueue.getInstance(context.getApplicationContext()).getImageLoader();
        imageLoader.get(url,ImageLoader.getImageListener(gambar,R.mipmap.ic_launcher,R.drawable.ic_launcher_background));
        gambar.setImageUrl(url,imageLoader);
        if (Integer.parseInt(model.get(position).getJumlah())==0){
            btnPinjam.setText("Habis");
            btnPinjam.setEnabled(false);
        }
        btnPinjam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnPinjam.isEnabled()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Peminjaman");
                    builder.setMessage("Kamu yakin ingin meminjam buku ini?");
                    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        return view;
    }

}
