package ua.edu.ucu.stream;

import ua.edu.ucu.function.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Function;

public class AsIntStream implements IntStream {

    InBuf inside;
    Pipeline operations;

    private class Pipeline{
        LinkedList operationsList;

        Pipeline(){
            this.operationsList = new LinkedList<>();
        }

        private void extendList(Function newOperation){
            this.operationsList.add(newOperation);
        }

    }

    private class InBuf {
        // as I think a good solution for a future optimisation
        // of an order of such operations

        ArrayList<Integer> innerBuffer;

        Long iteratorPosition; // instead of "Iterator" interface for buffered use



        InBuf(int ... args){
            for (int el: args){
                this.innerBuffer.add(el);
            }
        }

        private int cleanBuf(){
            int size = innerBuffer.size();
            innerBuffer.clear();
            return size;
        }

        private void add(int el){
            this.innerBuffer.add(el);
        }

        private  boolean hasNext(){
            throw new UnsupportedOperationException("Not supported yet.");
            //To change body of generated methods, choose Tools | Templates.
        }

        private Integer getNext(){
            throw new UnsupportedOperationException("Not supported yet.");
            //To change body of generated methods, choose Tools | Templates.
        }
    }


    private AsIntStream() { //has private constructor...
        //looks like never happens to be instantiated? functional...
        // To Do
        this.inside = new InBuf();
        this.operations = new Pipeline();
    }

    public static IntStream of(int... values) {
        //makes a stream for future needs, serves instead of a cunstructor
        AsIntStream newAsIntStream = new AsIntStream();
        for (int el: values){
            newAsIntStream.inside.add(el);
        }
        return newAsIntStream;
    }


    @Override
    public Double average() {
        throw new UnsupportedOperationException("Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer max() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer min() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer sum() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IntStream filter(IntPredicate predicate) {
        this.operations.extendList((Function) predicate);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void forEach(IntConsumer action) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IntStream flatMap(IntToIntStreamFunction func) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] toArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
