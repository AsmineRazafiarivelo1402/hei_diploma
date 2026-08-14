package com.hei.course.endpoint.event.consumer.model;

import com.hei.course.PojaGenerated;
import com.hei.course.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
