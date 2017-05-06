import javax.swing.plaf.synth.SynthEditorPaneUI;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Abhijeet Sidhu
 */
public class Huffman extends Object {

    public Huffman() {
    }


    private static class Node implements Comparable<Node> {
        private Integer character;
        private Integer frequency;
        private String code;
        private Node left, right;
        private boolean isLeaf;

        public int compareTo(Node n) {
            if (this.frequency.equals(n.frequency)) {
                return this.character.compareTo(n.character);
            } else {
                return this.frequency.compareTo(n.frequency);
            }
        }

        public Node(Node left, Node right) {
            this.character = 0;
            this.frequency = 0;
            this.left = left;
            this.right = right;
            if (left == null && right == null) {
                isLeaf = true;
            }
        }

        public Node() {
            this.character = 0;
            this.frequency = 0;
            this.code = "";
            isLeaf = true;
        }

        public int getFrequency() {
            return frequency;
        }

        public int getCharacter() {
            return character;
        }

//        public int getMin(Node n, PriorityQueue<Node> pq){
//            int chae = 0;
//            if (!n.isLeaf) {
//                getMin(n.left, pq);
//                getMin(n.right, pq);
//
//
//            }
//            if (n.isLeaf) {
//                chae = n.character;
//            }
//            return chae;
//        }
    }

    private static void makeCode(Node root, HashMap<Integer, Node> hm, String s) {
        if (!root.isLeaf) {
            makeCode(root.left, hm, s + "0");
            makeCode(root.right, hm, s + "1");
        }
        if (root.isLeaf) {
            int a = root.getCharacter();
            Node n = hm.get(a);
            n.code = s;
            hm.put(a, n);
        }
    }

    private static void StringMaker(Node root, PriorityQueue<Node> pq, StringBuilder sb) {
        if (!root.isLeaf) {
            StringMaker(root.left, pq, sb);
            StringMaker(root.right, pq, sb);
        }
        if (root.isLeaf) {
            int a = root.getCharacter();
            char z = (char)a;
            sb.append(z);
        }
    }

    public static String compress(String infileName, String outFileName) throws FileNotFoundException, IOException {
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        BufferedInputStream sc = new BufferedInputStream(new FileInputStream(new File(infileName)));
        HashMap<Integer, Node> hm = new HashMap<>();
        int a;
        while ((a = sc.read()) != -1) {
            int ascii = a;
            if (!hm.containsKey(ascii)) {
                Node n = new Node();
                n.character = ascii;
                n.frequency = 1;
                hm.put(ascii, n);
            } else {
                Node n = hm.get(ascii);
                n.frequency++;
                hm.put(ascii, n);
            }
        }
        sc.close();

//        Collection keys = hm.keySet();
//        Iterator<Node> through = keys.iterator();
//        while (through.hasNext()) {
//            Node n = hm.get(through.next());
//            pq.add(n);
//        }
        Set<Integer> newkeys = hm.keySet();
        Integer[] arr = new Integer[newkeys.size()];
        newkeys.toArray(arr);
        for (int item : arr){
            Node n = hm.get(item);
            pq.add(n);

        }

        //combine nodes and add back to pq
        while (pq.size() > 1) {
            Node first = pq.poll();
            Node second = pq.poll();
            Node n = new Node(first, second);
            n.frequency = first.frequency + second.frequency;
            if(first.character > second.character){
                n.character = second.character;
            }
            else{
                n.character = first.character;
            }
            pq.add(n);


        }

        //System.out.println("HERE: " + pq.peek().right.frequency);



        String s = "";

        if(pq.size() > 0){
            makeCode(pq.peek(), hm, s);
        }

        File file = new File(outFileName);

        FileOutputStream fs = new FileOutputStream(file);

        BufferedOutputStream bos = new BufferedOutputStream(fs);

        DataOutputStream dos = new DataOutputStream(bos);

        HuffmanBitStream hbs = new HuffmanBitStream(bos);

        //header
        dos.write((byte) hm.size());
        Set<Integer> newkey = hm.keySet();
        Integer[] arrs = new Integer[newkey.size()];
        newkey.toArray(arrs);
        Arrays.sort(arrs);
        for (int item : arrs){
            Node n = hm.get(item);
            int key = n.character;
            dos.write(key);
            int fre = n.frequency;
            dos.writeInt(fre);
        }

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(infileName)));
        //write to file
        int oh;
        while((oh = bis.read()) != -1){
            int asci = oh;
            Node n = hm.get(asci);
            String kode = n.code;
            for(int i = 0; i < kode.length(); i++){
               char bs = kode.charAt(i);
               hbs.write(bs);
            }
        }



        //close huffmanbitstream
        hbs.close();



        //create and return string
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        if(pq.size() > 0){
            StringMaker(pq.peek(), pq, sb);
        }
        else{
            dos.writeByte(0);
        }
        sb.append("|");
        return sb.toString();
    }

    public static void decompress(String infileName, String outFilename) throws FileNotFoundException, IOException {


        FileInputStream fs = new FileInputStream(infileName);
        BufferedInputStream bos = new BufferedInputStream(fs);
        DataInputStream dis = new DataInputStream(bos);
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        HuffmanBitStream hbs = new HuffmanBitStream(bos);

        byte total = dis.readByte();
        int allfreqs = 0;
        //int a = dis.read();
        while(total > 0) {
            int character = dis.readByte();
            int freq = dis.readInt();
            Node n = new Node();
            n.frequency = freq;
            allfreqs = allfreqs + freq;
            n.character = character;
            pq.add(n);
            total--;
        }




        while (pq.size() > 1) {
            Node first = pq.poll();

            Node second = pq.poll();
            Node n = new Node(first, second);
            n.frequency = first.frequency + second.frequency;
            if(first.character > second.character){
                n.character = second.character;
            }
            else{
                n.character = first.character;
            }
            pq.add(n);

        }





        PrintWriter pw = new PrintWriter(new File(outFilename));

        Node root = pq.peek();
        Node top = pq.peek();
        int counter = 0;
        while(counter < allfreqs){



            if(top.isLeaf){
                int c = top.character;
                //System.out.println("CHAR: "+ c);
                pw.write((char) c);
                counter++;
                top = root;
            }


            int the = hbs.read();
            if(top.left == null || top.right == null){
                top = top;
            }
            else {
                if (the == 0) {
                    top = top.left;
                } else {
                    top = top.right;
                }
            }



        }

        hbs.close();

        pw.close();










    }
}
