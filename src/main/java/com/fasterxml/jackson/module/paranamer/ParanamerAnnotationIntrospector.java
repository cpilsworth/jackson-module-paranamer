package com.fasterxml.jackson.module.paranamer;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.thoughtworks.paranamer.*;

/**
 * Stand-alone {@link AnnotationIntrospector} that defines functionality
 * to discover names of constructor (and factory method) parameters,
 * without using any defaulting.
 * Can be used as chainable add-on introspector.
 */
public class ParanamerAnnotationIntrospector
    extends NopAnnotationIntrospector
{
    private static final long serialVersionUID = 1;

    protected final Paranamer _paranamer;

    public ParanamerAnnotationIntrospector() {
        this(new CachingParanamer(new BytecodeReadingParanamer()));
    }

    public ParanamerAnnotationIntrospector(Paranamer pn) {
        _paranamer = pn;
    }

    @Override
    public String findDeserializationName(AnnotatedParameter param)
    {
        return _findParaName(param);
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated a)
    {
        if (a instanceof AnnotatedParameter) {
            String rawName = _findParaName((AnnotatedParameter) a);
            if (rawName != null) {
                return new PropertyName(rawName);
            }
        }
        return null;
    }

    /*
    /**********************************************************
    /* Internal methods
    /**********************************************************
     */

    protected String _findParaName(AnnotatedParameter param)
    {
        int index = param.getIndex();
        AnnotatedElement ctor = param.getOwner().getAnnotated();
        String[] names = _paranamer.lookupParameterNames((AccessibleObject) ctor, false);
        if (names != null) {
            if (index < names.length) {
                return names[index];
            }
        }
        return null;
    }
}
