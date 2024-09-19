package com.github.darkpred.multipartsupport.entity;

public interface MultiPartEntityHitResult {

    void multiPartSupport$setMultiPart(MultiPart<?> part);

    MultiPart<?> multiPartSupport$getMultiPart();
}
