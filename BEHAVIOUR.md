# Expected behaviour

## 1 Event logging

### 1.1 Create a new event from dropdowns

- Given that the program is selected
- Given that the four entities should be selected
- When the submit button is hit, a popup appears where the properties can be filled in.

### 1.2 Create a new event from predefined scenario

- Given that the program is selected
- When a scenario button is hit, a popup appears where the properties can be filled in.

### 1.3 Popup behaviour for new and existing events

- Strictly speaking a property is only tied to an event via the property entity. However, for simplicity we also 
  consider label, station and description to be properties. They should show up in the properties
  popup for final review.
- The same property should only appear once

#### 1.3.1 Popup behaviour for new events

- The properties of an event (definition) that was previously selected (via the 4 dropdowns) or clicked (via a scenario button) 
  may not reappear in a new, other event, neither the property keys, nor their values.

#### 1.3.2 Popup behaviour for existing events

- events have a detail page at event/uuid/edit
- When a scenario button is hit, a popup appears where the properties can be filled in.
- This popup should not appear when there are no real properties.
  As the label, station and description are to be completed in dedicated separate fields on top, there is no reason for
  the property popup if there are no true properties.
- The popup's fields should be populated with actual database values
- The popup's label, station and description fields should be exactly the same as the dedicated separate fields on top
- Modifying any and all properties should be persisted to the database, and their values must reappear in the detail page of the same event
