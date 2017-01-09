package ua.edu.ucu.stream;

import ua.edu.ucu.function.*;

import java.util.*;

public class AsIntStream implements IntStream {

    InBuf inside;
    Operations operations;

    DecoratorPipeline pipeline;

    private class DecoratorPipeline {
        DecoratorPipeline composition;
        MySuperIntFunction operationPerform;

        private DecoratorPipeline(){
        }

        private DecoratorPipeline(DecoratorPipeline toCompose){
            this.composition = toCompose;
        }

        private void addOperation(MySuperIntFunction function){
            pipeline = new DecoratorPipeline(this);
            pipeline.operationPerform = function;
        }

        private int[] getResult(int ... args){
            if (this.operationPerform == null) return args;
            int [] result;
            int size = 0;

            args = this.composition.getResult ( args );
            //digging into pipeline


            for (int arg: args) {

                if (operationPerform instanceof IntPredicate){
                    if (((IntPredicate) operationPerform).test(arg)){
                        result = new int[1];
                        result[0] = arg;
                        return result;
                    }
                    else return new int[0];
                }

                else if (operationPerform instanceof IntUnaryOperator){
                    result = new int[1];
                    result[0] = ((IntUnaryOperator) operationPerform).apply(arg);
                    return result;
                }

                else if (operationPerform instanceof IntToIntStreamFunction){
                    return ((IntToIntStreamFunction) operationPerform).applyAsIntStream(arg).toArray();
                }

            }
            return new int[0];
        }
    }



    private class Operations {
        LinkedList<MySuperIntFunction> operationsList;

        Integer iteratorPosition;

        Operations(){
            this.operationsList = new LinkedList<>();
            this.iteratorPosition = 0 ;
        }

        private boolean isEmpty(){
            return this.operationsList.isEmpty();
        }

        private void resetIterator(){
            this.iteratorPosition = 0;
        }

        private void add(MySuperIntFunction newOperation){
            this.operationsList.add(newOperation);
        }


}


    private void buildPipeline(){
        ListIterator<MySuperIntFunction> functionsIter = operations.operationsList.listIterator();

        pipeline = new DecoratorPipeline();

        if (!functionsIter.hasNext())
            return;

        while (functionsIter.hasNext()){
            pipeline.addOperation(functionsIter.next());
        }

    }

    private LinkedList <Integer> applyAll ( ) {
        buildPipeline ( );

        ListIterator <Integer> streamIter = inside.innerBuffer.listIterator ( );
        LinkedList <Integer> result = new LinkedList <> ( );

        int rawResult[];

        while ( streamIter.hasNext ( ) ) {
            rawResult = pipeline.getResult ( streamIter.next ( ) );

            for ( int el : rawResult ) {
                result.add ( el );
            }

        }

        return result;
    }

/*

    private LinkedList<Integer> applyAll(){
        ListIterator<Integer> streamIter = inside.innerBuffer.listIterator();
        Integer arg;
        while (streamIter.hasNext()){
            arg = streamIter.next();
            for (MySuperIntFunction func: operations.operationsList){
                //proceed all operations at once
                if (func instanceof IntPredicate){
                    arg = (((IntPredicate) func).test(arg) ? arg : null );
                    if (arg == null){
                        streamIter.remove();
                        break; //situation when no need to proceed
                    }
                }
                else if (func instanceof IntUnaryOperator)
                    streamIter.set( ((IntUnaryOperator) func).apply(arg) ); //unary operator
                else if (func instanceof IntToIntStreamFunction){
                    int pos = streamIter.nextIndex(); //position memorising
                    for (Integer el: ((IntToIntStreamFunction) func).applyAsIntStream(arg).toArray()){
                        streamIter.add(el);
                    }
                    while (streamIter.nextIndex() != pos)
                        streamIter.previous(); //REWINDING!
                }
            }
        }
        return inside.innerBuffer;
    }

        /*

        private ArrayList<Integer> applyAll(){
            //performs all non-terminal operations in task-list at once
            Integer arg = null;
            ArrayList <Integer> resultStream = new ArrayList<>();
            while (inside.hasNext()){
                arg = inside.getNext();
                for (MySuperIntFunction func: operationsList){
                    if (func instanceof IntPredicate){
                        arg = (((IntPredicate) func).test(arg)  ? arg : null );
                    }
                    else if (func instanceof IntUnaryOperator)
                        arg = ((IntUnaryOperator) func).apply(arg); //unary operator
                    else if (func instanceof IntToIntStreamFunction){
                        concat(((IntToIntStreamFunction) func).applyAsIntStream(arg));
                        arg = null; // no more needed and to avoid adding
                    }
                }
                if (arg != null)
                    resultStream.add(arg);
            }
            return resultStream;
        }
    }
*/
    private class InBuf {
        // as I think a good solution for a future optimisation
        // of an order of such operations

        LinkedList<Integer> innerBuffer;

        Integer iteratorPosition; // instead of "Iterator" interface for buffered use

        InBuf(int ... args){
            this.innerBuffer = new LinkedList<>();
            this.iteratorPosition = new Integer(0);
            for (int el: args){
                this.innerBuffer.add(el);
            }
        }

        private void  insertBuf(int[] arr, int position){
            for (Integer el: arr){
                innerBuffer.add(position++, el);
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
        }

        private Integer getNext(){
            iteratorPosition++;
            return innerBuffer.get( iteratorPosition-1);
        }

    }


    private IntStream concat(IntStream newIntStream){
        int [] contain = newIntStream.toArray();
        for (int el: contain){
            this.inside.innerBuffer.add(el);
        }
        return this;
    }

    private AsIntStream() { //has private constructor...
        //looks like never happens to be instantiated? functional...
        this.inside = new InBuf();
        this.operations = new Operations();
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
    public IntStream filter(IntPredicate predicate) { //non-terminal
        this.operations.add(predicate);
        return this;
    }

    @Override
    public void forEach(IntConsumer action) { //terminal
        Collection<? super Integer> preResults = this.applyAll();
        for (Object preResult : preResults) {
            action.accept((Integer) preResult);
        }
    }

    @Override
    public IntStream map(IntUnaryOperator mapper) { //non-terminal
        this.operations.add(mapper);
        return this;
    }

    @Override
    public IntStream flatMap(IntToIntStreamFunction func) { //non-terminal
        this.operations.add(func);
        return this;
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) { //terminal
        Collection<? super Integer> preResults = this.applyAll();
        for (Object preResult : preResults) {
            identity = op.apply(identity, (int)((Integer) preResult));
        }
        return identity;
    }

    @Override
    public int[] toArray() {
        int [] resArr = new int[this.inside.innerBuffer.size()];
        int i = 0;
        for (Integer el:this.inside.innerBuffer){
            resArr[i] = el;
            i++;
        }
        return resArr;
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
