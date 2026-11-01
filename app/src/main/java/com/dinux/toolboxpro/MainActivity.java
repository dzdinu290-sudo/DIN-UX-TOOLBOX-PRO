package com.dinux.toolboxpro;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQ_CAMERA = 501;
    private LinearLayout root, content;
    private SharedPreferences prefs;
    private boolean lightMode = false;
    private boolean flashlightOn = false;
    private String cameraId;

    private int bg() { return lightMode ? Color.rgb(246,243,235) : Color.rgb(9,10,13); }
    private int panel() { return lightMode ? Color.WHITE : Color.rgb(23,25,31); }
    private int fg() { return lightMode ? Color.rgb(28,28,31) : Color.rgb(246,242,230); }
    private int muted() { return lightMode ? Color.rgb(105,99,88) : Color.rgb(165,160,148); }
    private int gold() { return Color.rgb(214,179,90); }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("dinux_toolbox", MODE_PRIVATE);
        lightMode = prefs.getBoolean("light_mode", false);
        buildHome();
    }

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    private TextView text(String value, int size, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(fg());
        if (bold) t.setTypeface(null, android.graphics.Typeface.BOLD);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    private void pad(View v, int all) { v.setPadding(dp(all),dp(all),dp(all),dp(all)); }

    private void animate(View v) {
        Animation a = new AlphaAnimation(0f,1f);
        a.setDuration(360);
        v.startAnimation(a);
    }

    private TextView label(String s, int size, boolean bold) {
        TextView t = text(s,size,bold);
        t.setPadding(0,dp(5),0,dp(5));
        return t;
    }

    private Button button(String title, boolean primary, View.OnClickListener listener) {
        Button b = new Button(this);
        b.setText(title);
        b.setAllCaps(false);
        b.setTextSize(14);
        b.setTextColor(primary ? Color.rgb(22,20,16) : fg());
        if (Build.VERSION.SDK_INT >= 21) {
            b.setBackground(getDrawable(primary ? R.drawable.button_gold : R.drawable.panel_bg));
            b.setElevation(dp(primary ? 3 : 0));
        }
        b.setPadding(dp(10),dp(8),dp(10),dp(8));
        b.setOnClickListener(listener);
        return b;
    }

    private LinearLayout card() {
        LinearLayout c = column();
        c.setBackground(getDrawable(R.drawable.panel_bg));
        c.setPadding(dp(16),dp(14),dp(16),dp(14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
        lp.setMargins(0,dp(7),0,dp(7));
        c.setLayoutParams(lp);
        return c;
    }

    private void shell(String title, String subtitle) {
        root = column();
        root.setBackgroundColor(bg());
        root.setPadding(dp(18),dp(16),dp(18),dp(12));

        LinearLayout top = row();
        TextView brand = text("DINUX", 21, true);
        brand.setTextColor(gold());
        top.addView(brand, new LinearLayout.LayoutParams(0,-2,1));
        top.addView(button(lightMode ? "☾" : "☼", false, v -> {
            lightMode = !lightMode;
            prefs.edit().putBoolean("light_mode",lightMode).apply();
            buildHome();
        }));
        root.addView(top);

        TextView sub = label(title,25,true);
        sub.setPadding(0,dp(15),0,dp(3));
        root.addView(sub);
        TextView desc = label(subtitle,13,false);
        desc.setTextColor(muted());
        root.addView(desc);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        content = column();
        content.setPadding(0,dp(12),0,dp(18));
        scroll.addView(content);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
        animate(root);
    }

    private void buildHome() {
        shell("TOOLBOX PRO", "Your everyday tools. One premium space.");
        LinearLayout hero = card();
        hero.setBackgroundColor(panel());
        TextView h = label("✦  Smart tools for everyday life",18,true);
        h.setTextColor(gold());
        hero.addView(h);
        TextView p = label("Fast • Simple • Private • Useful",13,false);
        p.setTextColor(muted());
        hero.addView(p);
        content.addView(hero);

        TextView toolsTitle = label("YOUR TOOLS",12,true);
        toolsTitle.setTextColor(gold());
        content.addView(toolsTitle);

        String[][] tools = {
            {"📝","Notes","Save notes on this device"},
            {"🔢","Calculator","Quick everyday calculations"},
            {"🔤","Text Tools","Count words and change case"},
            {"▦","QR Generator","Create QR codes from text or links"},
            {"🔦","Flashlight","Use your phone camera flash"},
            {"🎨","Color Picker","Get HEX and RGB color values"},
            {"📦","Backup & Restore","Export or restore your notes"},
            {"📄","PDF Tools","Open your phone's PDF apps"},
            {"📁","File Manager","Browse files using Android"}
        };

        for (int i=0;i<tools.length;i+=2) {
            LinearLayout r = row();
            for (int j=i;j<Math.min(i+2,tools.length);j++) {
                final String name = tools[j][1];
                LinearLayout c = column();
                c.setBackground(getDrawable(R.drawable.panel_bg));
                c.setPadding(dp(13),dp(15),dp(12),dp(13));
                TextView icon = label(tools[j][0],25,true);
                c.addView(icon);
                TextView title = label(name,15,true);
                c.addView(title);
                TextView detail = label(tools[j][2],11,false);
                detail.setTextColor(muted());
                c.addView(detail);
                c.setOnClickListener(v -> openTool(name));
                c.setClickable(true);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,-2,1);
                lp.setMargins(0,dp(6),dp(6),dp(6));
                r.addView(c,lp);
            }
            content.addView(r);
        }

        LinearLayout foot = card();
        TextView f = label("DIN​​UX TOOLBOX PRO  •  Version 1.0",11,true);
        f.setTextColor(muted());
        foot.addView(f);
        content.addView(foot);
    }

    private void openTool(String name) {
        switch (name) {
            case "Notes": showNotes(); break;
            case "Calculator": showCalculator(); break;
            case "Text Tools": showTextTools(); break;
            case "QR Generator": showQr(); break;
            case "Flashlight": toggleFlashlight(); break;
            case "Color Picker": showColorPicker(); break;
            case "Backup & Restore": showBackup(); break;
            case "PDF Tools": launchIntent("application/pdf"); break;
            case "File Manager": launchFiles(); break;
            default: toast("Coming soon");
        }
    }

    private EditText edit(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint); e.setTextColor(fg()); e.setHintTextColor(muted());
        e.setTextSize(15);
        e.setPadding(dp(12),dp(8),dp(12),dp(8));
        e.setBackground(getDrawable(R.drawable.panel_bg));
        return e;
    }

    private void showNotes() {
        shell("NOTES", "Notes are stored locally on this device.");
        EditText note = edit("Write your note here...");
        note.setMinLines(5);
        note.setGravity(Gravity.TOP);
        note.setText(prefs.getString("notes",""));
        content.addView(note);
        content.addView(button("Save Note",true,v -> {
            prefs.edit().putString("notes",note.getText().toString()).apply();
            toast("Note saved on this device");
        }));
        content.addView(button("Back to Dashboard",false,v -> buildHome()));
    }

    private void showCalculator() {
        shell("CALCULATOR", "Basic arithmetic calculator");
        EditText expression = edit("Example: 125 * 3 + 10");
        expression.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        content.addView(expression);
        TextView result = label("Result will appear here",20,true);
        result.setTextColor(gold());
        content.addView(result);
        content.addView(button("Calculate",true,v -> {
            try {
                String s = expression.getText().toString().replace(" ","");
                double value = evaluateSimple(s);
                result.setText("Result: " + String.format(Locale.US,"%s",value));
            } catch (Exception e) {
                result.setText("Please enter a valid expression (+ - * / and brackets not supported).");
            }
        }));
        content.addView(button("Back to Dashboard",false,v -> buildHome()));
    }

    private double evaluateSimple(String s) {
        if (s.isEmpty() || !s.matches("[0-9.+*/-]+")) throw new IllegalArgumentException();
        // Small parser supporting operator precedence.
        java.util.ArrayList<Double> nums = new java.util.ArrayList<>();
        java.util.ArrayList<Character> ops = new java.util.ArrayList<>();
        int start=0;
        for (int i=1;i<s.length();i++) {
            char c=s.charAt(i);
            if (c=='+' || c=='-' || c=='*' || c=='/') {
                if (i==start || (c=='-' && (s.charAt(i-1)=='*'||s.charAt(i-1)=='/'))) continue;
                nums.add(Double.parseDouble(s.substring(start,i)));
                ops.add(c); start=i;
            }
        }
        nums.add(Double.parseDouble(s.substring(start)));
        for (int i=0;i<ops.size();) {
            char op=ops.get(i);
            if (op=='*'||op=='/') {
                double a=nums.get(i), b=nums.get(i+1);
                if (op=='/' && b==0) throw new ArithmeticException();
                nums.set(i,op=='*'?a*b:a/b);
                nums.remove(i+1); ops.remove(i);
            } else i++;
        }
        double result=nums.get(0);
        for (int i=0;i<ops.size();i++) result=ops.get(i)=='+'?result+nums.get(i+1):result-nums.get(i+1);
        return result;
    }

    private void showTextTools() {
        shell("TEXT TOOLS", "Count characters, words, and transform text.");
        EditText input = edit("Type or paste text...");
        input.setMinLines(4); input.setGravity(Gravity.TOP);
        content.addView(input);
        TextView output = label("Characters: 0 • Words: 0",14,true);
        content.addView(output);
        content.addView(button("Count Text",true,v -> {
            String s=input.getText().toString().trim();
            int words=s.isEmpty()?0:s.split("\\s+").length;
            output.setText("Characters: "+input.length()+" • Words: "+words);
        }));
        content.addView(button("UPPERCASE",false,v -> input.setText(input.getText().toString().toUpperCase(Locale.ROOT))));
        content.addView(button("lowercase",false,v -> input.setText(input.getText().toString().toLowerCase(Locale.ROOT))));
        content.addView(button("Copy Text",false,v -> {
            android.content.ClipboardManager cm=(android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            cm.setPrimaryClip(android.content.ClipData.newPlainText("text",input.getText().toString()));
            toast("Copied");
        }));
        content.addView(button("Back to Dashboard",false,v -> buildHome()));
    }

    private void showQr() {
        shell("QR GENERATOR", "Create a QR code from text or a website URL.");
        EditText input=edit("Enter text or https://example.com");
        content.addView(input);
        ImageView image=new ImageView(this);
        image.setAdjustViewBounds(true);
        image.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams imageLp=new LinearLayout.LayoutParams(dp(240),dp(240));
        imageLp.gravity=Gravity.CENTER;
        imageLp.setMargins(0,dp(15),0,dp(15));
        content.addView(image,imageLp);
        content.addView(button("Generate QR",true,v -> {
            String value=input.getText().toString().trim();
            if(value.isEmpty()){toast("Enter text first");return;}
            try {
                com.google.zxing.common.BitMatrix matrix=new QRCodeWriter().encode(value, BarcodeFormat.QR_CODE, 420,420);
                Bitmap bmp=Bitmap.createBitmap(420,420,Bitmap.Config.ARGB_8888);
                for(int x=0;x<420;x++) for(int y=0;y<420;y++) bmp.setPixel(x,y,matrix.get(x,y)?Color.BLACK:Color.WHITE);
                image.setImageBitmap(bmp);
            } catch(WriterException ex){toast("Could not create QR code");}
        }));
        content.addView(button("Back to Dashboard",false,v -> buildHome()));
    }

    private void toggleFlashlight() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { toast("Flashlight requires Android 6.0+"); return; }
        if (checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},REQ_CAMERA);
            return;
        }
        try {
            CameraManager manager=(CameraManager)getSystemService(Context.CAMERA_SERVICE);
            if(cameraId==null) {
                for(String id:manager.getCameraIdList()) {
                    android.hardware.camera2.CameraCharacteristics c=manager.getCameraCharacteristics(id);
                    Boolean flash=c.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer facing=c.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING);
                    if(Boolean.TRUE.equals(flash) && facing!=null && facing==android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK) {cameraId=id;break;}
                }
            }
            if(cameraId==null){toast("This device has no supported flashlight");return;}
            flashlightOn=!flashlightOn;
            manager.setTorchMode(cameraId,flashlightOn);
            toast(flashlightOn?"Flashlight ON":"Flashlight OFF");
        } catch(CameraAccessException ex) { toast("Flashlight unavailable"); }
    }

    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode==REQ_CAMERA && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) toggleFlashlight();
        else if(requestCode==REQ_CAMERA) toast("Camera permission is required for flashlight");
    }

    private void showColorPicker() {
        shell("COLOR PICKER", "Tap a color to get its HEX code.");
        int[] colors={0xFFE53935,0xFFFF9800,0xFFFFD54F,0xFF43A047,0xFF00BCD4,0xFF1E88E5,0xFF5E35B1,0xFFD81B60,0xFF000000,0xFFFFFFFF};
        TextView result=label("Choose a color",18,true);
        content.addView(result);
        for(int i=0;i<colors.length;i+=2){
            LinearLayout r=row();
            for(int j=i;j<Math.min(i+2,colors.length);j++){
                final int color=colors[j];
                Button b=button(String.format(Locale.US,"#%06X",0xFFFFFF & color),false,v->{
                    String hex=String.format(Locale.US,"#%06X",0xFFFFFF & color);
                    result.setText(hex+"  RGB("+Color.red(color)+", "+Color.green(color)+", "+Color.blue(color)+")");
                    result.setTextColor(color);
                    android.content.ClipboardManager cm=(android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(android.content.ClipData.newPlainText("HEX",hex));
                    toast(hex+" copied");
                });
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(58),1);
                lp.setMargins(0,dp(5),dp(5),dp(5)); r.addView(b,lp);
            }
            content.addView(r);
        }
        content.addView(button("Back to Dashboard",false,v -> buildHome()));
    }

    private void showBackup() {
        shell("BACKUP & RESTORE", "Export or restore the saved note text.");
        content.addView(label("Backup contains the Notes field saved by this app.",13,false));
        content.addView(button("Share Backup Text",true,v -> {
            String data="DINUX_TOOLBOX_PRO_BACKUP_V1\n"+prefs.getString("notes","");
            android.content.Intent intent=new android.content.Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain"); intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"DINUX Toolbox Backup");
            intent.putExtra(android.content.Intent.EXTRA_TEXT,data);
            startActivity(android.content.Intent.createChooser(intent,"Share backup"));
        }));
        content.addView(button("Restore from pasted backup",false,v -> {
            EditText e=edit("Paste backup text here");
            new AlertDialog.Builder(this).setTitle("Restore Backup").setView(e)
              .setPositiveButton("Restore",(d,w)->{
                  String data=e.getText().toString();
                  String marker="DINUX_TOOLBOX_PRO_BACKUP_V1\n";
                  if(data.startsWith(marker)){
                      prefs.edit().putString("notes",data.substring(marker.length())).apply();
                      toast("Notes restored");
                  } else toast("Invalid backup format");
              }).setNegativeButton("Cancel",null).show();
        }));
        content.addView(button("Back to Dashboard",false,v -> buildHome()));
    }

    private void launchIntent(String mime) {
        try {
            android.content.Intent i=new android.content.Intent(android.content.Intent.ACTION_VIEW);
            i.setType(mime);
            i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(android.content.Intent.createChooser(i,"Open with"));
        } catch(Exception e) { toast("No compatible app found"); }
    }

    private void launchFiles() {
        try {
            android.content.Intent i=new android.content.Intent(android.content.Intent.ACTION_GET_CONTENT);
            i.setType("*/*");
            startActivityForResult(android.content.Intent.createChooser(i,"Browse files"),700);
        } catch(Exception e) { toast("File browser unavailable"); }
    }

    private void toast(String s) { Toast.makeText(this,s,Toast.LENGTH_SHORT).show(); }
}
