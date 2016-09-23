package com.example.givemepass.filemanagerdemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String PRE_LEVEL = "..";
    public static final int FIRST_ITEM = 0;
    public static final int SECOND_ITEM = 1;
    private String IMG_ITEM = "image";
    private String NAME_ITEM = "name";
    private List<Map<String, Object>> filesList;
    private List<String> names;
    private List<String> paths;
    private File[] files;
    private Map<String, Object> filesMap;
    private int[] fileImg = {
            R.drawable.directory,
            R.drawable.file};
    private SimpleAdapter simpleAdapter;
    private ListView listView;
    private String nowPath;
    private TextView createDir;
    private TextView delFile;
    private TextView modifyName;
    private static final String[] ACTION = {"修改", "刪除"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private void initView() {
        simpleAdapter = new SimpleAdapter(this,
                filesList, R.layout.simple_adapter, new String[]{IMG_ITEM, NAME_ITEM},
                new int[]{R.id.image, R.id.text});
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String target = paths.get(position);
                if(target.equals(ROOT)){
                    nowPath = paths.get(position);
                    getFileDirectory(ROOT);
                    simpleAdapter.notifyDataSetChanged();
                } else if(target.equals(PRE_LEVEL)){
                    nowPath = paths.get(position);
                    getFileDirectory(new File(nowPath).getParent());
                    simpleAdapter.notifyDataSetChanged();
                } else {
                    File file = new File(target);
                    if (file.canRead()) {
                        if (file.isDirectory()) {
                            nowPath = paths.get(position);
                            getFileDirectory(paths.get(position));
                            simpleAdapter.notifyDataSetChanged();
                        } else{
                            Toast.makeText(MainActivity.this, R.string.is_not_directory, Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        Toast.makeText(MainActivity.this, R.string.can_not_read, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setItems(ACTION, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = ACTION[which];
                                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return true;
            }
        });
        createDir = (TextView) findViewById(R.id.new_dir);
        createDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewDir();
            }
        });
    }

    private void modifyName() {

    }

    private void delFile() {
        new AlertDialog.Builder(MainActivity.this)
            .setTitle("確定要刪除此檔案/資料夾?")
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
            })
            .show();
    }

    private void addNewDir(){
        final View item = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_new_dir, null);
        new AlertDialog.Builder(MainActivity.this)
            .setTitle(R.string.input_dir_name)
            .setView(item)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText editText = (EditText) item.findViewById(R.id.edittext);
                    if(editText.getText().equals("")){return;}
                    String filePath = nowPath + File.separator + editText.getText().toString();
                    File f = new File(filePath);
                    Toast.makeText(MainActivity.this, filePath, Toast.LENGTH_SHORT).show();

                    if(f.mkdir()){
                        Toast.makeText(MainActivity.this, getString(R.string.create_dir_success) + filePath, Toast.LENGTH_SHORT).show();
                        getFileDirectory(nowPath);
                        simpleAdapter.notifyDataSetChanged();
                    } else{
                        Toast.makeText(MainActivity.this, R.string.create_dir_fail, Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .show();
    }

    private void initData() {
        nowPath = ROOT;
        filesList = new ArrayList<>();
        names = new ArrayList<>();
        paths = new ArrayList<>();
        getFileDirectory(ROOT);
    }

    private void getFileDirectory(String path){
        filesList.clear();
        paths.clear();
        if(!path.equals(ROOT)){
            //回根目錄
            filesMap = new HashMap<>();
            names.add(ROOT);
            paths.add(FIRST_ITEM, ROOT);
            filesMap.put(IMG_ITEM, fileImg[0]);
            filesMap.put(NAME_ITEM, ROOT);
            filesList.add(filesMap);
            //回上一層
            filesMap = new HashMap<>();
            names.add(PRE_LEVEL);
            paths.add(SECOND_ITEM, new File(path).getParent());
            filesMap.put(IMG_ITEM, fileImg[0]);
            filesMap.put(NAME_ITEM, PRE_LEVEL);
            filesList.add(filesMap);
        }

        files = new File(path).listFiles();
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                filesMap = new HashMap<>();
                names.add(files[i].getName());
                paths.add(files[i].getPath());
                if (files[i].isDirectory()) {
                    filesMap.put(IMG_ITEM, fileImg[0]);
                } else {
                    filesMap.put(IMG_ITEM, fileImg[1]);
                }
                filesMap.put(NAME_ITEM, files[i].getName());
                filesList.add(filesMap);
            }
        }
    }
}
