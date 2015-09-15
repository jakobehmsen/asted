package asted.matching;

import java.util.Hashtable;

public class LocalsPattern {
    public static <T, R, S> Pattern<T, R> capture(String name, Pattern<T, S> target) {
        return (locals, input, output) -> {
            Buffer<Object> slot = locals.get(name);

            if(slot == null) {
                slot = Buffer.Util.create();
                locals.put(name, slot);
            }

            Hashtable<String, Buffer<Object>> newLocals = new Hashtable<>();
            target.matches(newLocals, input, (Buffer<S>)slot);

            return true;
        };
    }
}
