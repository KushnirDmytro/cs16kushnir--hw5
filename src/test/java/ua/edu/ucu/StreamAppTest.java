package ua.edu.ucu;

import org.junit.Before;
import org.junit.Test;
import ua.edu.ucu.stream.AsIntStream;
import ua.edu.ucu.stream.IntStream;

import static java.lang.Math.abs;
import static org.junit.Assert.*;

/**
 * @author andrii
 */
public class StreamAppTest {

    private IntStream intStream;

    @Before
    public void init ( ) {
        int[] intArr = {-1, 0, 1, 2, 3};
        intStream = AsIntStream.of ( intArr );
    }


    @Test(expected = IllegalArgumentException.class)
    public void testExeption ( ) {

        int[] intArr = {};
        intStream = AsIntStream.of ( intArr );
        System.out.println ( "streamExeptionTest" );
        intStream.average ( );
    }

    @Test
    public void testAvarageOperation ( ) {
        System.out.println ( "streamAvarageOperation" );
        double expResult = 1.0;
        double result = intStream.average ( );
        assertTrue ( abs ( expResult - result ) < 0.0001 );
    }

    @Test
    public void testStreamOperations ( ) {
        System.out.println ( "streamOperations" );
        int expResult = 42;
        int result = StreamApp.streamOperations ( intStream );
        assertEquals ( expResult, result );
    }


    @Test
    public void testMaxStream ( ) {
        System.out.println ( "streamFullCountOperator" );
        int res = intStream
                .filter ( x -> x > 0 ) // 1, 2, 3
                .map ( x -> x * x ) // 1, 4, 9
                .flatMap ( x -> AsIntStream.of ( x - 1, x, x + 1 ) ) // 0, 1, 2, 3, 4, 5, 8, 9, 10
                .max ( ); // 42
        int expResult = 10;
        assertEquals ( expResult, res );
    }

    @Test
    public void testMinStream ( ) {
        System.out.println ( "streamFullCountOperator" );
        int res = intStream
                .min ( ); // 42
        int expResult = -1;
        assertEquals ( expResult, res );
    }


    @Test
    public void testFullCountOperations ( ) {
        System.out.println ( "streamFullCountOperator" );
        long res = intStream
                .filter ( x -> x > 0 ) // 1, 2, 3
                .map ( x -> x * x ) // 1, 4, 9
                .flatMap ( x -> AsIntStream.of ( x - 1, x, x + 1 ) ) // 0, 1, 2, 3, 4, 5, 8, 9, 10
                .count ( ); // 42
        int expResult = 9;
        assertEquals ( expResult, res );
    }


    @Test
    public void testJustCountOperations ( ) {
        System.out.println ( "streamCountOperator" );
        long res = intStream
                .count ( ); // 5
        int expResult = 5;
        assertEquals ( expResult, res );
    }

    @Test
    public void testSumOperations ( ) {
        System.out.println ( "streamSumOperator" );
        int res = intStream
                .sum ( ); // 5
        int expResult = 5;
        assertEquals ( expResult, res );
    }


    @Test
    public void testStreamToArray ( ) {
        System.out.println ( "streamToArray" );
        int[] expResult = {-1, 0, 1, 2, 3};
        int[] result = StreamApp.streamToArray ( intStream );
        assertArrayEquals ( expResult, result );
    }

    @Test
    public void testStreamForEach ( ) {
        System.out.println ( "streamForEach" );
        String expResult = "-10123";
        String result = StreamApp.streamForEach ( intStream );
        System.out.println ( result );
        assertEquals ( expResult, result );
    }

}
