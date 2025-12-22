package org.example.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.example.entity.Cell;
import org.example.entity.Edge;
import org.example.entity.Line;
import org.example.entity.Point;

public class DeepCopyHelper<T> {

    private static final Kryo kryo = new Kryo();

    static {
        kryo.register(Point.class);
        kryo.register(Edge.class);
        kryo.register(Cell.class);
        kryo.register(Line.class);
    }

    public T copy(T object) {
        Output output = new Output(4096, -1);
        kryo.writeClassAndObject(output, object);

        Input input = new Input(output.getBuffer(), 0, output.position());
        return (T) kryo.readClassAndObject(input);
    }
}