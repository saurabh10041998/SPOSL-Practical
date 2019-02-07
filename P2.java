import java.io.*;
import java.util.*;

class KPD {
        String label,value;
        
        public KPD(String label , String value){
                this.label = label;
                this.value = value;
        }

        public String toString() {
            return label+"   "+value;
        }
}

class  Macro_data {
	String name;
	int pp, kp, mdtp, kpdtp;

	public Macro_data(String name, int pp, int kp,int mdtp, int kpdtp) {

		this.name = name;
		this.pp = pp;
		this.kp = kp;
		this.mdtp = mdtp;
		this.kpdtp = kpdtp;
	}
    public String toString() {
        return name+"  "+pp+"  "+kp+"  "+mdtp+"  "+kpdtp;
    }
}

public class P2 {
        public static void main(String []args) throws IOException { 
                
                BufferedReader buffer = new BufferedReader(new FileReader(new File("input2.txt")));
                Pass2(buffer);
        }
        
        
        public static void Pass2(BufferedReader buffer) throws IOException {
                //pntab for each seperate macro
                List<String>pntab = null;
                String str = "";
                //open file for mdtptr writing
                FileWriter fout = new FileWriter("mdt.txt");
                //common KPDTable
                List<KPD>kpdtab = new ArrayList<>();
                //common mnt table 
                List<Macro_data>MNT = new ArrayList<>();
                //filewriter 
                FileWriter fd = null;
                int kpd_ptr = 0;
                int mdt_ptr = 1;
                int mnt_ptr = 0;
                int pntab_ptr = -1;
                int kp = -1, pp = -1;
                boolean is_prototype = true;
                while((str = buffer.readLine()) != null) {
                        str = str.trim();
                        if(str.equalsIgnoreCase("MACRO")){
                                        is_prototype = true;
                                        //create the pntab 
                                        pntab = new ArrayList<>();
                                        pp = kp = 0;
                                        //indication that next job to be done is prototype processing
                                        continue;
                        }
                        else if(is_prototype) {
                                        str = str.replaceAll("[&,]", " ");
                                        str = str.replaceAll("\\s+", " ");
                                        str = str.trim();
                                        
                                        String data[] = str.split("\\s+");
                                        String name = data[0];
                                        fd = new FileWriter("pntab_"+name+".txt");
                                        data = Arrays.copyOfRange(data, 1, data.length);
                                        boolean value = false;
                                        // create the PNTAB                                        
                                        for(String x:data) {
                                                if(x.equalsIgnoreCase("=")){
                                                                //keyword paramter 
                                                     value = true;                                               
                                                     continue;           
                                                }
                                                else if(value) {
                                                	kpdtab.add(new KPD(pntab.get(pntab.size()-1), x));
                                                    kpd_ptr++;
                                                    kp++;
                                                    value = false;
                                                }
                                                else{
                                                    //positional parameter
                                                	pntab.add(x);
                                                	pp++;
                                                }
                                        }
                                        MNT.add(new Macro_data(name,pp-kp, kp, mdt_ptr,kpd_ptr-kp));
                                        is_prototype = false;                                        
                        }
                        else if(str.equals("MEND")) {
                           fout.write(mdt_ptr+"    "+"MEND\n");
                           mdt_ptr++;
                           for(String n:pntab) {
                                fd.write(n+"\n");
                           } 
                           fd.close();                           
                        }
                        else {
                            //Model statement processing..
                            str = str.replaceAll(",", " ");
                            str = str.replaceAll("\\s+", " ");
                            str = str.trim();

                            String string_to_write = "";

                            String data[] = str.split("\\s+");
                            for(String x: data) {
                                if(x.indexOf("&") == -1) {
                                        string_to_write += x + "  ";
                                }else{
                                    int index = pntab.indexOf(x.substring(1).trim());
                                    string_to_write += "( P,"+index+")  ";
                                }
                            }
                            fout.write(mdt_ptr+"    "+string_to_write+"\n");
                            mdt_ptr++;
                        }
                        
                }
                fout.close();
                fd = new FileWriter("kpdtab.txt");
                for(KPD x:kpdtab) {
                    fd.write(x+"\n");
                }
                fd.close();
                fd = new FileWriter("mnttab.txt");
                for(Macro_data x:MNT) {
                    fd.write(x+"\n");
                }
                fd.close();
                System.out.println("Pass 1 jhala bhava!!!");                 
                
        }
}
