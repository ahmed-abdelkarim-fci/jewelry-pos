package com.jewelry.pos.core.validation;

import jakarta.validation.groups.Default;

public interface ValidationGroups {
    
    // Use this group when creating a new record (e.g. ID should be null)
    interface Create extends Default {}

    // Use this group when updating a record (e.g. ID must not be null)
    interface Update extends Default {}
}