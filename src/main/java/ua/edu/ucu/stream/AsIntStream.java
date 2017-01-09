package ua.edu.ucu.stream;

import ua.edu.ucu.function.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class AsIntStream implements IntStream {

    InBuf inside;
    Operations operations;
    DecoratorPipeline pipeline;

    private class DecoratorPipeline {
        DecoratorPipeline composition;
        MySuperIntFunction operationPerform;

        private DecoratorPipeline ( ) {
        }

        private DecoratorPipeline (DecoratorPipeline toCompose) {
            this.composition = toCompose;
        }

        private void addOperation (MySuperIntFunction function) {
            pipeline = new DecoratorPipeline ( this );
            pipeline.operationPerform = function;
        }

        private int[] getResult (int... args) {
            if (this.operationPerform == null) return args;

            args = this.composition.getResult ( args );
            //digging into pipeline

            for ( int arg : args ) {

                if (operationPerform instanceof IntPredicate) {
                    if (((IntPredicate) operationPerform).test ( arg )) {
                        return new int[]{arg};
                    } else return new int[0];

                } else if (operationPerform instanceof IntUnaryOperator) {
                    return new int[] {((IntUnaryOperator) operationPerform).apply ( arg )};

                } else if (operationPerform instanceof IntToIntStreamFunction) {
                    return ((IntToIntStreamFunction) operationPerform).applyAsIntStream ( arg ).toArray ( );

                }

            }
            return new int[0];
        }
    }


    private class Operations {
        List <MySuperIntFunction> operationsList;

        Integer iteratorPosition;

        Operations ( ) {
            this.operationsList = new LinkedList <> ( );
            this.iteratorPosition = 0;
        }

        private boolean isEmpty ( ) {
            return this.operationsList.isEmpty ( );
        }

        private void resetIterator ( ) {
            this.iteratorPosition = 0;
        }

        private void add (MySuperIntFunction newOperation) {
            this.operationsList.add ( newOperation );
        }


    }


    private void buildPipeline ( ) {
        ListIterator <MySuperIntFunction> functionsIter = operations.operationsList.listIterator ( );

        pipeline = new DecoratorPipeline ( );

        if (!functionsIter.hasNext ( ))
            return;

        while ( functionsIter.hasNext ( ) ) {
            pipeline.addOperation ( functionsIter.next ( ) );
        }

    }

    private LinkedList <Integer> applyAll ( ) {
        buildPipeline ( );

        ListIterator <Integer> streamIter = (ListIterator <Integer>) inside.innerBuffer.listIterator ( );
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

    private class InBuf {
        // as I think a good solution for a future optimisation
        // of an order of such operations

        List <? super Integer> innerBuffer;

        Integer iteratorPosition; // instead of "Iterator" interface for buffered use

        InBuf (int... args) {
            this.innerBuffer = new LinkedList <> ( );
            this.iteratorPosition = new Integer ( 0 );
            for ( int el : args ) {
                this.innerBuffer.add ( el );
            }
        }

        private void insertBuf (int[] arr, int position) {
            for ( Integer el : arr ) {
                innerBuffer.add ( position++, el );
            }
        }

        private int cleanBuf ( ) {
            int size = innerBuffer.size ( );
            innerBuffer.clear ( );
            return size;
        }

        private void add (int el) {
            this.innerBuffer.add ( el );
        }

        private boolean hasNext ( ) {
            return iteratorPosition < innerBuffer.size ( );
        }

        private Integer getNext ( ) {
            iteratorPosition++;
            return (Integer) innerBuffer.get ( iteratorPosition - 1 );
        }

    }


    private IntStream concat (IntStream newIntStream) {
        int[] contain = newIntStream.toArray ( );
        for ( int el : contain ) {
            this.inside.innerBuffer.add ( el );
        }
        return this;
    }

    private AsIntStream ( ) { //has private constructor...
        //looks like never happens to be instantiated? functional...
        this.inside = new InBuf ( );
        this.operations = new Operations ( );
    }

    public static IntStream of (int... values) {
        //makes a stream for future needs, serves instead of a cunstructor
        AsIntStream newAsIntStream = new AsIntStream ( );
        for ( int el : values ) {
            newAsIntStream.inside.add ( el );
        }
        return newAsIntStream;
    }


    private Collection <? super Integer> terminator ( ) {
        //instead of 1000 checks

        Collection <? super Integer> preResults = this.applyAll ( );

        if (preResults.isEmpty ( ))
            throw new IllegalArgumentException ( );

        return preResults;
    }


    @Override
    public Double average ( ) { //terminal & dependant
        final long[] sum = {0};
        final long[] counter = {0l};

        forEach ( x -> {
            counter[0]++;
            sum[0] += x;
        } );

        return (double) (sum[0] / counter[0]);
    }


    @Override
    public Integer min ( ) {//terminal & dependant
        return reduce ( 0, (min, x) -> (min < x) ? min : x );
    }

    @Override
    public Integer max ( ) {//terminal & dependant
        return reduce ( 0, (max, x) -> (max > x) ? max : x );
    }

    @Override
    public long count ( ) { //terminal & dependant
        final long[] counter = {0l};
        forEach ( x -> counter[0]++ );
        return counter[0];
    }

    @Override
    public Integer sum ( ) { //terminal & dependant
        return reduce ( 0, (sum, x) -> sum += x );
    }

    @Override
    public IntStream filter (IntPredicate predicate) { //non-terminal
        this.operations.add ( predicate );
        return this;
    }

    @Override
    public IntStream flatMap (IntToIntStreamFunction func) { //non-terminal
        this.operations.add ( func );
        return this;
    }

    @Override
    public IntStream map (IntUnaryOperator mapper) { //non-terminal
        this.operations.add ( mapper );
        return this;
    }

    @Override
    public void forEach (IntConsumer action) { //terminal
        Collection <? super Integer> preResults = terminator ( );
        for ( Object preResult : preResults ) {
            action.accept ( (Integer) preResult );
        }
    }


    @Override
    public int reduce (int identity, IntBinaryOperator op) { //terminal

        Collection <? super Integer> preResults = terminator ( );

        for ( Object preResult : preResults ) {
            identity = op.apply ( identity, (Integer) preResult );
        }
        return identity;
    }

    @Override
    public int[] toArray ( ) {
        int[] resArr = new int[this.inside.innerBuffer.size ( )];
        int i = 0;
        for ( Object el : this.inside.innerBuffer ) {
            resArr[i] = (int) el;
            i++;
        }
        return resArr;
    }

}


//preserved for the future generations



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
