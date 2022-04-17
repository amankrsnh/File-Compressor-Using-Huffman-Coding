package com.lnct.huffmanencoder;
import java.util.*;
import java.io.*;
public class HuffmanEncoder 
{
    final static int ALPHABETS=256;
    
    public static HuffmanResult compress(String data) //O(n)
    {
        int []frequencies=countFrequency(data); //Time: O(len(data))  //Space: O(256)
        HuffmanNode root=createBinaryTree(frequencies); //O(256) //Space: O(distinct(element))
        Map<Character,String> map= new HashMap<>();
        mapFrequency(root,"",map);  //O(distinct(element))
        String edata=encodeData(data,map);
        HuffmanResult result=new HuffmanResult(root,edata,convertEncodedDataToAscii(edata));   //O(distinct(element))
        return result;
    }
    
    public static String decompress(HuffmanResult data) //O(len(encodedData))
    {
        String bin=AsciiToBinary(data.getAscii());
        int dellength=bin.length()-data.encodedData.length();
        System.out.println(dellength);
        bin=bin.substring(dellength,bin.length());
        HuffmanNode root=data.getRoot();
        StringBuilder sb=new StringBuilder();
        int i=0;
        while(i<bin.length())
        {
            while(!root.isLeaf())
            {
                char bit=bin.charAt(i);
                if(bit=='1')
                    root=root.right;
                else if(bit=='0')
                    root=root.left;
                else
                    throw new IllegalArgumentException("Invalid bit in message! "+bit);
                ++i;                                                                                             
            }
            sb.append(root.ch);
            root=data.getRoot();
        }
        return sb.toString();
    }
    
    private static int[] countFrequency(String data) 
    {
        int []freq=new int[256];
        for(char c:data.toCharArray())
            freq[c]++;
        return freq;
    }

    private static HuffmanNode createBinaryTree(int[] freq) 
    {
        PriorityQueue<HuffmanNode> list=new PriorityQueue<>();
        for(int i=0;i<ALPHABETS;++i)
        {
            if(freq[i]>0)
                list.add(new HuffmanNode((char)i,freq[i],null,null));
        }
        if(list.size()==1)
            list.add(new HuffmanNode('\0',1,null,null));
        while(list.size()>1)
        {
            HuffmanNode l=list.poll();
            HuffmanNode r=list.poll();
            HuffmanNode parent=new HuffmanNode('\0',l.freq+r.freq,l,r);
            list.add(parent);
        }
        return list.poll();
    }
    
    private static void mapFrequency(HuffmanNode node,String bits,Map<Character,String> map)
    {
        if(!node.isLeaf())
        {
            mapFrequency(node.left,bits+"0",map);
            mapFrequency(node.right,bits+"1",map);
        }
        else
            map.put(node.ch, bits);
    }
    
    private static String encodeData(String data,Map<Character,String> map)
    {
        StringBuilder sb=new StringBuilder();
        for(char c:data.toCharArray())
        {
            sb.append(map.get(c));
        }
        return sb.toString();
    }
    
    public static class HuffmanNode implements Comparable<HuffmanNode>
    {
        char ch;
        int freq;
        HuffmanNode left;
        HuffmanNode right;
        
        public HuffmanNode(char ch,int freq,HuffmanNode left,HuffmanNode right)
        {
            this.ch=ch;
            this.freq=freq;
            this.left=left;
            this.right=right;
        }
        
        public int compareTo(HuffmanNode that)
        {
            if(this.freq==that.freq)
                return Integer.compare(this.ch, that.ch);
            return this.freq-that.freq;
        }
        
        public boolean isLeaf()
        {
            return this.left==null && this.right==null;
        }
        
    }
    
    static class HuffmanResult
    {
        private HuffmanNode root;
        private String encodedData;
        private String ascii;
        public HuffmanResult(HuffmanNode root, String encodedData,String ascii) {
            this.root = root;
            this.encodedData = encodedData;
            this.ascii=ascii;
        }
        
        public HuffmanNode getRoot() {
            return root;
        }

        public String getEncodedData() {
            return encodedData;
        }
        public String getAscii()
        {
            return ascii;
        }
        
    }
    static class FileData
    {
        File file;
        FileData(String path)
        {
            file=new File(path);
        }
        File getEncodedFilePath()
        {
            System.out.println(file.getPath());
            return new File(file.getParent()+"\\"+file.getName().substring(0, file.getName().lastIndexOf("."))+"_encoded.huff");
        }
        File getDecodedFilePath()
        {
            return new File(file.getParent()+"\\"+file.getName().substring(0, file.getName().lastIndexOf("."))+"_decoded.txt");
        }
        String readFileData() throws IOException
        {
            BufferedReader br=new BufferedReader(new FileReader(file));
            String st;
            StringBuilder text=new StringBuilder();
            while((st=br.readLine())!=null)
                text.append(st);
            return text.toString();
        }
    }

    public static int convertByteToAscii(String bytes)
    {
        int ans=0,base=1;
        for(int i=bytes.length()-1;i>=0;--i)
        {
            if(bytes.charAt(i)=='1')
                ans+=base;
            base=base*2;
        }
        return ans;
    }

    public static String convertEncodedDataToAscii(String bin)
    {
        if(bin.length()%8!=0)
        {
            int rem=8-bin.length()%8;
            for(int i=0;i<rem;++i)
                bin="0"+bin;
        }
        StringBuilder res=new StringBuilder();
        for(int i=0;i<bin.length();i+=8 )
        {
            res.append((char)convertByteToAscii(bin.substring(i, i+8)));
        }
        return res.toString();
    }

    public static String AsciiToBinary(String s)
    {
        int n = s.length();
        StringBuilder ans=new StringBuilder();
        for (int i = 0; i < s.length(); i++)
        {
            int val = Integer.valueOf(s.charAt(i));
            StringBuilder bin = new StringBuilder();
            while (val > 0)
            {
                if (val % 2 == 1)
                {
                    bin.append('1');
                }
                else
                    bin.append('0');
                val /= 2;
            }
            for(int j=bin.length();j<8;++j)
                bin.append('0');
            bin.reverse();
            ans.append(bin);
        }
        return ans.toString();
    }

    public static void main(String[] args) throws Exception
    {
        HuffmanEncoder encoder=new HuffmanEncoder();
        FileData fd=new FileData("C:\\Users\\HP\\OneDrive\\Desktop\\text1.txt");
        String data=fd.readFileData();    //"Hello Everyone! My name is Aman Kumar Singh. This is my minor project in java which is based on Huffman Coding. Keep Smiling!";
        System.out.println(data);
        System.out.println("Size of actual data: "+data.length()*8+" bytes");
        
        long startCompressing = System.currentTimeMillis();
        HuffmanResult result=encoder.compress(data);
        long endCompressing = System.currentTimeMillis();
       
        System.out.println("Time to compress the data: "+(endCompressing-startCompressing)+" ms");
        System.out.println("Size of compressed data: "+result.getAscii().length()*8+" bytes");

        System.out.println("Binary: "+result.getEncodedData());
        System.out.println("BinaryToAscii: "+result.getAscii());
        
        System.out.println();
        File binFile=fd.getEncodedFilePath();
        try
        {
            if(!binFile.exists())
                binFile.createNewFile();
            FileWriter bw=new FileWriter(binFile);
            bw.write(result.getAscii());
            bw.close();
        }
        catch(IOException io)
        {
            System.out.println("Encoded File Can't be created!");
        }
        
        
        long startDeCompressing = System.currentTimeMillis();
        String decodedData=decompress(result);
        long endDeCompressing = System.currentTimeMillis();
        
        File decodedFile=fd.getDecodedFilePath();
        try
        {
            if(!decodedFile.exists())
                decodedFile.createNewFile();
            FileWriter bwriter=new FileWriter(decodedFile);
            bwriter.write(decodedData);
            bwriter.close();
        }
        catch(IOException io)
        {
            System.out.println("Decoded File Can't be created!");
        }
        
        System.out.println("Time to Decompress the data: "+(endDeCompressing-startDeCompressing+" ms"));
        System.out.println(decodedData);
        System.out.println("Size of decoded data: "+decodedData.length()*8);
        
        System.out.println("Size Reduced%: "+(100*(data.length()*8-result.getAscii().length()*8)/(data.length()*8)));
        
    }
}