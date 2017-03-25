package com.example.ericlouw.jinjectsu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exceptions.CyclicDependencyException;

public class CyclicDependencyChecker {
    private Map<Class, List<Class>> dependencyMap;

    CyclicDependencyChecker(){
        this.dependencyMap = new HashMap<>();
    }

    void registerDependency(Class type, List<Class> dependencies) {
        this.dependencyMap.put(type, dependencies);
        
        this.checkCyclicDependencies(type);
    }

    private void checkCyclicDependencies(Class type) {
        for(Map.Entry<Class, List<Class>> entry : this.dependencyMap.entrySet()){
            if(this.hasCyclicDependency(entry.getKey(), new HashSet<Class>())){
                throw new CyclicDependencyException(String.format("Cyclic dependency detected when registering type %s", type.getName()));
            }
        }
    }

    private boolean hasCyclicDependency(Class type, Set<Class> typeTrail){
        if(typeTrail.contains(type)){
            return true;
        }

        typeTrail.add(type);

        if(!this.dependencyMap.containsKey(type)){
            return false;
        }

        List<Class> dependencies = this.dependencyMap.get(type);

        if(dependencies == null || dependencies.size() == 0){
            return false;
        }

        for(Class dependency : dependencies){
            return hasCyclicDependency(dependency, typeTrail);
        }

        return false;
    }
}
