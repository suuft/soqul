package net.soqul;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.soqul.impl.SoqulImpl;

@UtilityClass
public class SoqulProvider {
    private static Soqul instance;

    public Soqul get() {
        if (instance == null) register(new SoqulImpl());
        return instance;
    }

    protected void register(@NonNull Soqul soqul) {
        instance = soqul;
    }
}
