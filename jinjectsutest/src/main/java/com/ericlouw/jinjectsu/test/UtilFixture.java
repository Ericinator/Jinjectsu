package com.ericlouw.jinjectsu.test;

import com.ericlouw.jinjectsu.jinjectsu.utils.ListUtilWrapper;
import com.ericlouw.jinjectsu.test.testmodels.TestConcreteA;
import com.ericlouw.jinjectsu.test.testmodels.TestConcreteB;
import com.ericlouw.jinjectsu.test.testmodels.TestConcreteC;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UtilFixture {
    @Test
    public void givenList_WhenConvertToDelimitedString_ConvertsCorrectly(){
        List<Class> classes = new ArrayList<>();
        classes.add(TestConcreteA.class);
        classes.add(TestConcreteB.class);
        classes.add(TestConcreteC.class);

        ListUtilWrapper<Class> wrapper = new ListUtilWrapper<>(classes);

        String delimitedString = wrapper.toDelimitedString(",");

        Assert.assertEquals(delimitedString, "class com.ericlouw.jinjectsu.test.testmodels.TestConcreteA,class com.ericlouw.jinjectsu.test.testmodels.TestConcreteB,class com.ericlouw.jinjectsu.test.testmodels.TestConcreteC");
    }
}
