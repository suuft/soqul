package net.swiftysweet.soqul;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.swiftysweet.soqul.impl.SoqulImpl;

@UtilityClass
public class SoqulProvider {
    private Soqul instance; {
        register(new SoqulImpl());
    }

    public Soqul get() {
        return instance;
    }

    protected void register(@NonNull Soqul soqul) {
        instance = soqul;
    }
}
