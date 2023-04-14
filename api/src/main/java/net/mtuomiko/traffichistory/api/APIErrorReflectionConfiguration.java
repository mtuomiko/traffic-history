package net.mtuomiko.traffichistory.api;

import net.mtuomiko.traffichistory.gen.model.ErrorWrapper;

import io.quarkus.runtime.annotations.RegisterForReflection;

// Register generated error model for native build
@RegisterForReflection(targets = {ErrorWrapper.class})
public class APIErrorReflectionConfiguration {
}
