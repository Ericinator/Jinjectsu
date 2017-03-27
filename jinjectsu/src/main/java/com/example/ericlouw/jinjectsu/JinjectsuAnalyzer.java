package com.example.ericlouw.jinjectsu;

import java.util.HashSet;
import java.util.Set;

import exceptions.UnregisteredTypeException;

public class JinjectsuAnalyzer {
    private Jinjectsu jinjectsu;

    public JinjectsuAnalyzer(Jinjectsu jinjectsu) {
        this.jinjectsu = jinjectsu;
    }

    public boolean validateTypeRegistration() {
        Set<Class> allRegisteredTypes = new HashSet<>();

        allRegisteredTypes.addAll(this.jinjectsu.singletonContainer.getRegisteredTypes());
        allRegisteredTypes.addAll(this.jinjectsu.transientContainer.getRegisteredTypes());
        allRegisteredTypes.addAll(this.jinjectsu.instanceContainer.getRegisteredTypes());
        allRegisteredTypes.addAll(this.jinjectsu.scopedContainer.getRegisteredTypes());

        for (Class type : allRegisteredTypes) {
            Class concreteType = this.jinjectsu.getTypeRegisteredUnder(type);

            Class[] dependencies = this.jinjectsu.getConstructorDependenciesForType(concreteType);

            for (Class dependency : dependencies) {

                if(this.jinjectsu.registrationTypeMap.containsKey(dependency)){
                    continue;
                }

                try {
                    if (!allRegisteredTypes.contains(dependency)) {
                        throw new UnregisteredTypeException(String.format("Type %s was not registered.", dependency.getName()));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    public boolean dryRun() {
        Set<Class> allRegisteredTypes = new HashSet<>();

        allRegisteredTypes.addAll(this.jinjectsu.singletonContainer.getRegisteredTypes());
        allRegisteredTypes.addAll(this.jinjectsu.transientContainer.getRegisteredTypes());
        allRegisteredTypes.addAll(this.jinjectsu.instanceContainer.getRegisteredTypes());
        allRegisteredTypes.addAll(this.jinjectsu.scopedContainer.getRegisteredTypes());

        for (Class type : allRegisteredTypes) {
           try{
                this.jinjectsu.resolve(type);
           }
           catch(Exception e){
               e.printStackTrace();
               return false;
           }
        }
        return true;
    }

}
