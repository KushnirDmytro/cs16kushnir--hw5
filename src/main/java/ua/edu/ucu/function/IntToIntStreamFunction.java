package ua.edu.ucu.function;

import ua.edu.ucu.stream.IntStream;

public interface IntToIntStreamFunction extends MySuperIntFunction{
     //accepts int value and produces Stream of it
     IntStream applyAsIntStream(int value);
}
