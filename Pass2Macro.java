import java.util.*;
import java.io.*;

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
public class Pass2Macro {
	public static void main(String []args) throws IOException{

		Pass2("INCR_D A, B, REG = BREG");

	}

	public static void Pass2(String macro_call) throws IOException{
			//load the KPD table and MNT table
			BufferedReader buffer = new BufferedReader(new FileReader(new File("mnttab.txt")));

			List<Macro_data>MNT = new ArrayList<>();
			List<KPD>kpdtab = new ArrayList<>();
			String data = "";
			while((data = buffer.readLine()) != null){
				data = data.trim();
				String []y = data.split("\\s+");
				MNT.add(new Macro_data(y[0], Integer.parseInt(y[1]), Integer.parseInt(y[2]), Integer.parseInt(y[3]),Integer.parseInt(y[4])));				
			}
			buffer.close();

			buffer = new BufferedReader(new FileReader(new File("kpdtab.txt")));

			while((data = buffer.readLine()) != null) {
				data = data.trim();
				String []y = data.split("\\s+");
				kpdtab.add(new KPD(y[0], y[1]));
			}

			buffer.close();

			macro_call = macro_call.replaceAll("[,=]", " ");
			macro_call = macro_call.replaceAll("\\s+", " ");
			macro_call = macro_call.trim();

			String call_data[] = macro_call.split("\\s+");

			String name_of_macro = call_data[0];
			Macro_data mnt_data = getMacroData(name_of_macro, MNT);
			if(mnt_data == null) {
				System.out.println("Invalid Macro Call");
				return;

			}else {
				//Do processing
				String []aptab = new String[mnt_data.pp + mnt_data.kp];
				//copying the keyword parameter value..
				int j = 0;
				for(int i = mnt_data.pp; i< (mnt_data.pp + mnt_data.kp); i++) {
					aptab[i] = kpdtab.get(mnt_data.kpdtp + j).value;
					j++;
				}
				//processing the call
				call_data = Arrays.copyOfRange(call_data, 1, call_data.length);				 
				for(int i=0;i<mnt_data.pp;i++) {
						aptab[i] = call_data[i];
				}
				boolean value = false; 		//boolean flag which keeps track of key and value status
				int index = -1;				//index within the aptab.
				for(int i = mnt_data.pp; i<call_data.length;i++) {
						//search for keyword parameter
						if(!value) {
							String key = call_data[i];
							index = find_key(kpdtab, key,mnt_data);
							value = true;
						}
						else {
							//System.out.println(index);
							aptab[mnt_data.pp + index] = call_data[i];
							value = false;
							index = -1;
						}
				}				 
				buffer = new BufferedReader(new FileReader(new File("mdt.txt")));
				String code = "";
				while((code = buffer.readLine()) != null) {
					code = code.trim();
					String [] codedata = code.split("\\s+");
					if(Integer.parseInt(codedata[0]) == mnt_data.mdtp){
						break;
					}
				}
				FileWriter fout = new FileWriter("expcode.txt");
				while(!code.substring(1).trim().equals("MEND")) {
					code = code.substring(1).trim();
					String[]codedata = code.split("\\s+");

					String string_to_write = "";
					for(String x: codedata) {
						if(x.indexOf("(") == -1) {
							string_to_write += x +"  ";
						}else {
							x = x.replaceAll("\\W", " ");
							x = x.trim();
							x = x.replaceAll("\\s+", " ");
							String []xdata = x.split("\\s+");
							int pointer = Integer.parseInt(xdata[1]);
							string_to_write += aptab[pointer] + " ";
						}
					}
					fout.write("+  	"+string_to_write+"\n");					
					code = buffer.readLine();
				}

				fout.close();



		}
}
	public static Macro_data getMacroData(String name, List<Macro_data>MNT) {
		for(int i=0;i<MNT.size();i++) {
			Macro_data sample_obj = MNT.get(i);
			if (sample_obj.name.equals(name))
				return sample_obj;
		}
		return null;
	}
	public static int find_key(List<KPD>kpdtab, String key,Macro_data mnt_data) {

		for(int i = mnt_data.kpdtp; i < mnt_data.kp ; i++) {
			if(kpdtab.get(i).label.equals(key))
				return i;
		}
		return -1;

	}
}