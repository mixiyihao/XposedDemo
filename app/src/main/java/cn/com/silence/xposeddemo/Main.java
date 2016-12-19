package cn.com.silence.xposeddemo;


import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {

    public void writeData(LinkedList data) {
        try {
            File file = new File("/sdcard/log","data.txt");
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), "utf-8"));
            pw.print("-------start------" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis())));
            for (Object object : data) {
                pw.print("=====人====");
                Class<?> aClass = object.getClass();
                Field[] fields = aClass.getFields();
                for (Field filed : fields) {
                    String name = filed.getName();
                    Object value_object = filed.get(object);
                    String value = String.valueOf(value_object);
                    if ("jVS".equals(name) || "jVT".equals(name)) {
                        Class<?> _class = value_object.getClass();
                        value="@";
                        for (Field jvsField : _class.getFields()) {
                            String _name = jvsField.getName();
                            String _value = String.valueOf(jvsField.get(value_object));
                            value += _name + ":" + _value+"\t";
                        }
                    }
                    pw.println(name + ":" + value);
                    pw.flush();
                }
            }
            pw.print("--------------end!-----------------------");
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static String getBuild(String value) {
        String cpu_cout = null;
        try {
            Class<?> aClass = Class.forName("android.os.SystemProperties");
            Method get = aClass.getMethod("get", String.class);
            cpu_cout = (String) get.invoke(null, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpu_cout;
    }

    /**
     * 对于多个dex的hook方法
     *
     * @param classLoader
     */

    public void hookMutiClound(ClassLoader classLoader) {
     /*   XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI", classLoader, "aB", List.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Object arg = param.args[0];
                XposedBridge.log("-----aB----" + arg + "-----------");
                //658400886
                super.beforeHookedMethod(param);
            }
        });*/
       /* XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.nearby.a.d", classLoader, "amP", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                LinkedList result = (LinkedList) param.getResult();
                writeData(result);
            }
        });*/
        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.ui.ap", classLoader, "c",boolean.class,List.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
               List list = (List) param.args[1];
               XposedBridge.log("list:"+list);
                for(Object object:list){
                    Class<?> aClass = object.getClass();
                    aClass.getFields();

                }
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.i.k", classLoader, "aCD", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                Class<?> aClass = result.getClass();
                Field[] fields = aClass.getFields();
                for(Field field:fields){
                    String name = field.getName();
                    Object o = field.get(result);
                    XposedBridge.log("name:"+name+";;;;;value:"+o);
                }
            }
        });
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!"com.tencent.mm".equals(loadPackageParam.packageName)) {
            //
            return;
        }
        XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.l", loadPackageParam.classLoader, int.class, List.class, List.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("paramInt:" + param.args[0]);
                XposedBridge.log("paramList1:" + param.args[1]);
                List<String> list = (List<String>) param.args[1];
                String s = list.get(0);
                XposedBridge.log("paramList1->String:" + s);
                XposedBridge.log("paramList2:" + param.args[2]);
                XposedBridge.log("String:" + param.args[3]);
                XposedBridge.log("String:" + param.args[4]);
                Log.e("hook", "neibor:" + s + ",content:" + param.args[3]);
                //vl_bc0597aee5a7929caa94d9111045aeb3416c7b818c1a4c240f0de14fff8745d3@stranger
                // list.set(0,"vl_bc0597aee5a7929caa94d9111045aeb3416c7b818c1a4c240f0de14fff8745d3@stranger");
                super.beforeHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.modelsimple.x", loadPackageParam.classLoader, String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("x:contruct:" + param.args[0]);
                XposedBridge.log("x:int :" + param.args[1]);
                super.beforeHookedMethod(param);
            }
        });
        //getStringExtra
        XposedHelpers.findAndHookMethod("android.content.Intent", loadPackageParam.classLoader, "getStringExtra", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String name = (String) param.args[0];
                boolean flag = false;
                if ("Contact_User".equals(name)) {
                    flag = true;
                } else if ("Contact_Alias".equals(name)) {
                    flag = true;
                } else if ("Contact_Encryptusername".equals(name)) {
                    flag = true;
                } else if ("Contact_Search_Mobile".equals(name)) {
                    flag = true;
                } else if ("Contact_Nick".equals(name)) {
                    flag = true;
                } else if ("Verify_ticket".equals(name)) {
                    flag = true;
                }
                if (flag) {
                    String result = (String) param.getResult();
                    XposedBridge.log(name + ":" + result);
                    Log.e("hook", "getStringExtra:" + name + ",result:" + result);
                }

                super.afterHookedMethod(param);
            }
        });
       /* XposedHelpers.findAndHookMethod("android.os.Process", loadPackageParam.classLoader, "killProcess", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.args[0]=1000000;
                XposedBridge.log("pid"+param.args[0]);
                super.beforeHookedMethod(param);
            }
        });*/

        XposedHelpers.findAndHookMethod("android.app.Application", loadPackageParam.classLoader, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Context context = (Context) param.args[0];
                hookMutiClound(context.getClassLoader());
            }
        });

        /*XposedHelpers.findAndHookMethod("com.tencent.mm.modelsimple.x", loadPackageParam.classLoader, "uG" ,String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("uG:"+param.args[0]);
                super.beforeHookedMethod(param);
            }
        });*/
      /*  XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.g", loadPackageParam.classLoader, "ma" ,String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("ma:"+param.args[0]);
                super.beforeHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.profile.ui.g$1", loadPackageParam.classLoader, "gz" ,int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("gz:"+param.args[0]);
                super.beforeHookedMethod(param);
            }
        });*/
     /*   XposedHelpers.findAndHookMethod("com.tencent.mm.model.at", loadPackageParam.classLoader, "dk", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("\nmixi --dk--- arge ----"+param.args[0]);
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if(result != null && result instanceof String){
                    XposedBridge.log("\nmixi --after--- dk ----return = "+result.toString());
                }else{
                    XposedBridge.log("\nmixi -after--dk--  return null----");
                }
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mm.compatible.d.p", loadPackageParam.classLoader, "na", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String cpu_count = getBuild("cpu_count");
                if(cpu_count == null || "unknown".equals(cpu_count)){
                    param.setResult(1);
                }else{
                    param.setResult(Integer.valueOf(cpu_count));
                }
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookConstructor("java.lang.ProcessBuilder", loadPackageParam.classLoader, String[].class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if(param.args[0] != null){
                    String[] args = (String[]) param.args[0];
                    if(args.length <2){
                        return;
                    }

                    if(args[0] != null && ("/system/bin/cat".equals(args[0]))){

                        String fileCommand = (String)args[1];
                        if(fileCommand != null && "/proc/meminfo".equals(fileCommand)){
                            if(new File("/data/meminfo").exists()){
                                args[1]="/data/meminfo";
                            }
                        }else if(fileCommand != null && "/proc/cpuinfo".equals(fileCommand)){
                            if(new File("/proc/cpuinfo").exists()){
                                args[1]="/data/cpuinfo";
                            }
                        }else if(fileCommand != null && "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq".equals(fileCommand)){
                            if(new File("/data/cpuinfo_max_freq").exists()){
                                args[1]="/data/cpuinfo_max_freq";
                            }
                        }
                    }
                    param.args[0] =args;
                }

                super.beforeHookedMethod(param);


            }
        });
        XposedHelpers.findAndHookMethod("java.net.NetworkInterface", loadPackageParam.classLoader, "getHardwareAddress", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String hardwareAddress = getBuild("HardwareAddress");
                String[] split = hardwareAddress.split(",");
                byte[] address = null;
                if(split.length <= 1){
                    address=new byte[]{02,00,00,00,00};
                }else{
                    address = new byte[split.length];
                    for(int i=0;i<split.length;i++){
                        address[i] = Byte.valueOf(split[0]);
                    }
                }
                XposedBridge.log("HardwareAddress:"+ Arrays.toString(address));
                param.setResult(address);
            }
        });*/
        /*XposedHelpers.findAndHookMethod("com.tencent.smtt.export.external.libwebp", loadPackageParam.classLoader, "getCPUinfo", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if(result != null && result instanceof String){
                    XposedBridge.log("\nmixi ---after-- getCPUinfo ----return = "+result.toString());
                }else {
                    XposedBridge.log("mixi---after----getCPUinfo--getCPUinfo = null");
                }
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mm.compatible.d.m", loadPackageParam.classLoader, "mK", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if(result != null && result instanceof HashMap){
                    HashMap map = new HashMap();
                    map.put("Processor","AArch64 Processor rev 2 (aarch64)");
                    map.put("model name","AArch64 Processor rev 2 (aarch64)");
                    map.put("BogoMIPS","26.00");
                    map.put("Features","fp asimd evtstrm aes pmull sha1 sha2 crc32");
                    map.put("CPU implementer","0x41");
                    map.put("CPU revision","2");
                    map.put("CPU variant","0x0");
                    map.put("CPU part","0xd03");
                    map.put("Hardware","MT6755M");
                    param.setResult(map);
                }else {
                }
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mm.compatible.d.p", loadPackageParam.classLoader, "mZ", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if(result != null && result instanceof HashMap){
                    HashMap map = new HashMap();
                    map.put("Processor","AArch64 Processor rev 2 (aarch64)");
                    map.put("model name","AArch64 Processor rev 2 (aarch64)");
                    map.put("BogoMIPS","26.00");
                    map.put("Features","fp asimd evtstrm aes pmull sha1 sha2 crc32");
                    map.put("CPU implementer","0x41");
                    map.put("CPU revision","2");
                    map.put("CPU variant","0x0");
                    map.put("CPU part","0xd03");
                    map.put("Hardware","MT6755M");
                    param.setResult(map);
                }else {
                }
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mm.compatible.d.p", loadPackageParam.classLoader, "mS", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if(result != null) {
                    String[] bbb = (String[]) result;
                    XposedBridge.log("---after--mS----"+Arrays.toString(bbb));
                }
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mm.model.at", loadPackageParam.classLoader, "d",byte[].class,int.class,int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("-----d---------"+param.args[0]+","+param.args[1]+","+param.args[2]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if(result != null) {
                    XposedBridge.log("---after--d----"+result.toString());
                }
            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mm.model.NorMsgSource", loadPackageParam.classLoader, "checkMsgLevel", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if(result != null && result instanceof Boolean){
                    Boolean is = (Boolean) result;
                    XposedBridge.log("-----checkMsgLevel---------"+is);
                }
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mm.model.NorMsgSource", loadPackageParam.classLoader, "checkSoftType",String.class,int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("----before checkSoftTypes---"+param.args[0]+","+param.args[1]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if(result == null){
                    return;
                }
                byte[] bytes = (byte[]) result;
                XposedBridge.log(new String(bytes));
                XposedBridge.log("----after checkSoftType--result-"+new String(bytes));            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mm.model.NorMsgSource", loadPackageParam.classLoader, "checkSoftType2",Context.class,String.class,int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("----before checkSoftTypes2---"+param.args[0]+","+param.args[1]+","+param.args[2]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if(result == null){
                    return;
                }
                byte[] bytes = (byte[]) result;
                XposedBridge.log(new String(bytes));
                XposedBridge.log("----after checkSoftType2--result-"+new String(bytes));            }
        });
        XposedHelpers.findAndHookMethod("com.tencent.mm.model.NorMsgSource", loadPackageParam.classLoader, "checkSoftType3", Context.class,new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("----before checkSoftTypes3---"+param.args[0]);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                if(result == null){
                    return;
                }
                byte[] bytes = (byte[]) result;
                XposedBridge.log("----after checkSoftType3--result-"+new String(bytes));
            }
        });
        */
    }


}
