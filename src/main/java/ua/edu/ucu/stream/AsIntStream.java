package ua.edu.ucu.stream;

import ua.edu.ucu.function.*;

import java.util.*;

public class AsIntStream implements IntStream {


    InBuf inside;
    Pipeline operations;

    private class Pipeline{
        LinkedList<MyIntFunction> operationsList;

        Integer iteratorPosition;

        Pipeline(){
            this.operationsList = new LinkedList<>();
            this.iteratorPosition = 0 ;
        }

        private boolean isEmpty(){
            return this.operationsList.isEmpty();
        }

        private void resetIterator(){
            this.iteratorPosition = 0;
        }

        private void add(MyIntFunction newOperation){
            this.operationsList.add(newOperation);
        }

        private ArrayList<Integer> applyAll(){
            //performs all nonterminal operations in task-list at once
            Integer arg = null;
            ArrayList <Integer> resultStream = new ArrayList<>();
            while (inside.hasNext()){
                arg = inside.getNext();
                for (MyIntFunction func: operationsList){
                    if (func.getClass() == IntPredicate.class){
                        arg = (func.test(arg) ? arg : null );
                        if (arg == null) break;
                    }
                    else arg = func.apply(arg); //unary operator
                }
                resultStream.add(arg);
            }
            return resultStream;
        }
    }

    private class InBuf {
        // as I think a good solution for a future optimisation
        // of an order of such operations

        ArrayList<Integer> innerBuffer;

        Integer iteratorPosition; // instead of "Iterator" interface for buffered use

        InBuf(int ... args){
            this.innerBuffer = new ArrayList<Integer>();
            this.iteratorPosition = new Integer(0);
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
            return iteratorPosition < innerBuffer.size();
            //To change body of generated methods, choose Tools | Templates.
        }

        private Integer getNext(){
            iteratorPosition++;
            return innerBuffer.get( iteratorPosition-1);
            //throw new UnsupportedOperationException("Not supported yet.");
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
        return this.inside.innerBuffer.size();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer sum() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IntStream filter(IntPredicate predicate) {
        //non-terminal
        this.operations.add(predicate);
        return this;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void forEach(IntConsumer action) {
        //terminal
        Collection<? extends Integer> preResults = this.operations.applyAll();
        for (Object preResult : preResults) {
            action.accept((Integer) preResult);
        }
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
