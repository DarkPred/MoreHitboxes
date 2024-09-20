package com.github.darkpred.multipartsupport.entity;
//API
public interface MultiPartEntityHitResult {

    void multiPartSupport$setMultiPart(MultiPart<?> part);

    MultiPart<?> multiPartSupport$getMultiPart();
}
