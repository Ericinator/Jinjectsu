package com.ericlouw.jinjectsu.jinjectsu.interfaces;

import com.ericlouw.jinjectsu.jinjectsu.Jinjectsu;

public interface IScopeContextBinder {
    Jinjectsu satisfiedBy(Class... concreteScopeContexts);
}
