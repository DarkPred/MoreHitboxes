package com.github.darkpred.multipartsupport;

import com.github.darkpred.multipartsupport.entity.MultiPart;

public interface MultiPartEntityHitResult {

    void multiPartSupport$setMultiPart(MultiPart<?> part);

    MultiPart<?> multiPartSupport$getMultiPart();
}
