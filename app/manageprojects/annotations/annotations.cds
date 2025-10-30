using szakdolgozat.srv.service.ProjectService as service from '../../../srv/services/ProjectService';

annotate service.Projects with @(

    //enable the create, edit button and disable the delete button on the list report page
    odata.draft.enabled,
    UI.UpdateHidden                          : false,
    Capabilities.DeleteRestrictions.Deletable: false,
    Capabilities.UpdateRestrictions.Updatable: true,

    //List report page - Fields that are displayed by default
    UI.LineItem                              : [
        {
            $Type: 'UI.DataField',
            Value: name
        },
        {
            $Type: 'UI.DataField',
            Value: description
        },
        {
            $Type: 'UI.DataField',
            Value: status_ID
        },
        {
            $Type: 'UI.DataField',
            Value: type_ID
        },
        {
            $Type : 'UI.DataFieldForAnnotation',
            Target: 'manager/@Communication.Contact',
            Label : 'Manager'
        },
        {
            $Type: 'UI.DataField',
            Value: startDate
        },
        {
            $Type                : 'UI.DataFieldWithUrl',
            Url                  : link,
            Value                : 'Open',
            Label                : 'Link',
            ![@HTML5.CssDefaults]: {width: '6%'}
        },
        {
            $Type: 'UI.DataField',
            Value: lastStatusChangeAt
        },
        {
            $Type             : 'UI.DataFieldForAction',
            Label             : 'Change Status',
            Action            : 'szakdolgozat.srv.service.ProjectService.changeStatus',
            InvocationGrouping: #Isolated
        },
        {
            $Type         : 'UI.DataFieldForIntentBasedNavigation',
            SemanticObject: 'managestatuses',
            Action        : 'launch',
            Label         : 'Manage Statuses',
            Inline        : false
        },
        {
            $Type         : 'UI.DataFieldForIntentBasedNavigation',
            SemanticObject: 'managetype',
            Action        : 'launch',
            Label         : 'Manage Types',
            Inline        : false
        }
    ],

    //Object page - Section Definitions
    UI.Facets                                : [
        {
            $Type : 'UI.ReferenceFacet',
            ID    : 'ProjectDetails',
            Label : 'Project Details',
            Target: '@UI.FieldGroup#ProjectDetails',
        },
        {
            $Type : 'UI.ReferenceFacet',
            ID    : 'ProjectHistory',
            Label : 'Project History',
            Target: 'snapshots/@UI.LineItem',
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Administration',
            ID    : 'Administration',
            Target: '@UI.FieldGroup#Administration'
        }
    ],

    //Object Page - Header
    UI.HeaderInfo                            : {
        Title         : {
            $Type: 'UI.DataField',
            Value: name
        },
        TypeName      : 'Project',
        TypeNamePlural: 'Projects'
    },

    //Object Page - Project Details - Field Definitions
    UI.FieldGroup #ProjectDetails            : {
        $Type: 'UI.FieldGroupType',
        Data : [
            {
                $Type: 'UI.DataField',
                Value: name,
                Label: 'Project Name'
            },
            {
                $Type: 'UI.DataField',
                Value: description
            },
            {
                $Type                : 'UI.DataField',
                Value                : type_ID,
                ![@HTML5.CssDefaults]: {width: '10rem'},
                ![UI.Hidden]         : {$edmJson: {$Path: 'IsActiveEntity'}}
            },
            {
                $Type: 'UI.DataField',
                Value: status_ID
            },
            {
                $Type                : 'UI.DataField',
                Value                : manager_ID,
                ![@HTML5.CssDefaults]: {width: '10rem'},
                @UI.Hidden           : {$edmJson: {$Path: 'IsActiveEntity'}}
            },
            {
                $Type                : 'UI.DataFieldForAnnotation',
                Target               : 'manager/@Communication.Contact',
                Label                : 'Manager',
                ![@HTML5.CssDefaults]: {width: '10rem'},
                @UI.Hidden           : {$edmJson: {$Not: {$Path: 'IsActiveEntity'}}}
            },
            {
                $Type: 'UI.DataField',
                Value: lastStatusChangeAt,
            },
            {
                $Type: 'UI.DataField',
                Value: startDate
            },
            {
                $Type: 'UI.DataField',
                Value: link,
            }
        ]
    },
    //Object Page - Administration - Field Definitions
    UI.FieldGroup #Administration            : {
        $Type: 'UI.FieldGroupType',
        Data : [
            {
                $Type: 'UI.DataField',
                Value: createdAt
            },
            {
                $Type: 'UI.DataField',
                Value: createdBy
            },
            {
                $Type: 'UI.DataField',
                Value: modifiedAt
            },
            {
                $Type: 'UI.DataField',
                Value: modifiedBy
            }
        ]
    },

    //Change status button in object page
    UI.Identification                        : [{
        $Type       : 'UI.DataFieldForAction',
        Action      : 'szakdolgozat.srv.service.ProjectService.changeStatus',
        Label       : 'Change Status',
        Criticality : #Neutral,
        ![UI.Hidden]: {$edmJson: {$Not: {$Path: 'IsActiveEntity'}}}
    }]
);

annotate service.Projects with @(

    //List report page - Default filters to filter by
    UI.SelectionFields           : [
        status_ID,
        type_ID,
        manager_ID,
        status.isFinalStatus,
        startDate
    ],

    //List report page - Filter Group Definitions
    UI.FilterFacets              : [
        {
            Target: '@UI.FieldGroup#Basic',
            Label : 'Basic'
        },
        {
            Target: '@UI.FieldGroup#Administrative',
            Label : 'Administrative'
        }
    ],

    //List report page - Defining filters for the different filter groups
    UI.FieldGroup #Basic         : {Data: [
        {Value: status_ID},
        {Value: type_ID},
        {Value: manager_ID},
        {Value: startDate}
    ]},
    UI.FieldGroup #Administrative: {Data: [
        {Value: createdBy},
        {Value: createdAt},
        {Value: modifiedAt},
        {Value: modifiedBy}
    ]}
) {
    //Project fields annotations
    ID                      @(
        UI.Hidden      : true,
        UI.HiddenFilter: true
    );

    createdAt               @(
        UI.Hidden      : false,
        UI.HiddenFilter: false
    );

    createdBy               @(
        UI.Hidden      : false,
        UI.HiddenFilter: false,
        Common         : {
            ValueListWithFixedValues: false,
            ValueList               : {
                Label         : 'Created By',
                CollectionPath: 'User',
                Parameters    : [{
                    $Type            : 'Common.ValueListParameterInOut',
                    ValueListProperty: 'email',
                    LocalDataProperty: createdBy
                }]
            },
            Text                    : createdBy,
            TextArrangement         : #TextOnly,
        }
    );
    modifiedAt              @(
        UI.Hidden      : false,
        UI.HiddenFilter: false
    );
    modifiedBy              @(
        UI.Hidden      : false,
        UI.HiddenFilter: false,
        Common         : {
            ValueListWithFixedValues: false,
            ValueList               : {
                Label         : 'Created By',
                CollectionPath: 'User',
                Parameters    : [{
                    $Type            : 'Common.ValueListParameterInOut',
                    ValueListProperty: 'email',
                    LocalDataProperty: modifiedBy
                }]
            },
            Text                    : modifiedBy,
            TextArrangement         : #TextOnly,
        }
    );
    name                    @(
        UI.Hidden      : false,
        UI.HiddenFilter: true,
    );
    description             @(
        UI.Hidden      : false,
        UI.HiddenFilter: true,
    );
    link                    @(
        UI.Hidden      : true,
        UI.HiddenFilter: true,
    );
    startDate               @(
        UI.Hidden      : false,
        UI.HiddenFilter: false,
    );
    status                  @(
        UI.Hidden      : false,
        UI.HiddenFilter: false,
        Common         : {
            FieldControl            : statusFieldAvailability,
            ValueListWithFixedValues: true,
            ValueList               : {
                Label         : 'Project Status',
                CollectionPath: 'Status',
                Parameters    : [{
                    $Type            : 'Common.ValueListParameterInOut',
                    ValueListProperty: 'ID',
                    LocalDataProperty: 'status_ID'
                }]
            },
            Text                    : status.name,
            TextArrangement         : #TextOnly
        }
    );
    type                    @(
        UI.Hidden      : false,
        UI.HiddenFilter: false,
        Common         : {
            ValueListWithFixedValues: true,
            ValueList               : {
                Label         : 'Project Status',
                CollectionPath: 'Type',
                Parameters    : [{
                    $Type            : 'Common.ValueListParameterInOut',
                    ValueListProperty: 'ID',
                    LocalDataProperty: 'type_ID'
                }]
            },
            Text                    : type.name,
            TextArrangement         : #TextOnly
        }
    );
    manager                 @(
        UI.Hidden      : false,
        UI.HiddenFilter: false,
        Common         : {
            ValueListWithFixedValues: false,
            ValueList               : {
                Label         : 'Project Status',
                CollectionPath: 'User',
                Parameters    : [{
                    $Type            : 'Common.ValueListParameterInOut',
                    ValueListProperty: 'ID',
                    LocalDataProperty: 'manager_ID'
                }]
            },
            Text                    : manager.displayName,
            TextArrangement         : #TextOnly
        }
    );
    lastStatusChangeAt      @(
        UI.Hidden      : false,
        UI.HiddenFilter: true,
    );
    statusFieldAvailability @(UI.Hidden: true);
    completedAt             @(
        UI.Hidden      : true,
        UI.HiddenFilter: true,
    );
} actions {
    changeStatus(
    @(Common.SideEffects: {TargetEntities: ['/ProjectService.EntityContainer/Projects']})

    newStatus
                               @Common: {
        FieldControl            : #Mandatory,
        ValueListWithFixedValues: true,
        ValueList               : {
            Label         : 'Project Status',
            CollectionPath: 'Status',
            Parameters    : [{
                $Type            : 'Common.ValueListParameterInOut',
                ValueListProperty: 'ID',
                LocalDataProperty: newStatus
            }]
        },
        Label                   : 'New Project Status',
    },
    changeDate
                               @Common: {
        FieldControl: #Optional,
        Label       : 'Date of Change',
    }
    );
};

annotate service.ProjectSnapshot with @(
    //Object Page - Defines a list report table for project history section
    UI.LineItem : [
        {
            $Type                : 'UI.DataField',
            Value                : statusName,
            ![@HTML5.CssDefaults]: {width: '20rem'}
        },
        {
            $Type                : 'UI.DataField',
            Value                : createdAt,
            ![@HTML5.CssDefaults]: {width: '20rem'}
        },
        {
            $Type                : 'UI.DataField',
            Value                : createdBy,
            ![@HTML5.CssDefaults]: {width: '20rem'}
        }
    ],
    Capabilities: {
        InsertRestrictions.Insertable: false,
        DeleteRestrictions.Deletable : false
    }
) {
    ID          @UI.Hidden: true;
    status      @UI.Hidden: true   @UI.HiddenFilter: true;
    project     @UI.Hidden: true   @UI.HiddenFilter: true;
    statusName  @UI.Hidden: false  @UI.HiddenFilter: true  @Common.FieldControl: project.statusFieldAvailability;
    createdBy   @UI.Hidden: false;
    modifiedBy  @UI.Hidden: true;
    modifiedAt  @UI.Hidden: true;
};


annotate service.Projects with @(
                                 //Enable calender on date filters
                               Capabilities.FilterRestrictions: {FilterExpressionRestrictions: [
    {
        Property          : 'startDate',
        AllowedExpressions: 'SingleRange'
    },
    {
        Property          : 'createdAt',
        AllowedExpressions: 'SingleRange'
    },
    {
        Property          : 'modifiedAt',
        AllowedExpressions: 'SingleRange'
    }
]});

annotate service.Type with {
    ID @Common: {
        Text           : name,
        TextArrangement: #TextOnly
    }
};

annotate service.Status with {
    ID             @Common: {
        Text           : name,
        TextArrangement: #TextOnly
    };

    name           @UI.Hidden: false  @UI.HiddenFilter: true;
    isFinalStatus  @UI.Hidden: false  @UI.HiddenFilter: true;
};

annotate service.User with @(Communication.Contact: {
    fn     : displayName,
    kind   : #individual,
    email  : [{
        type   : #work,
        address: email
    }],
    tel    : [{
        type: #work,
        uri : phone
    }],
    orgunit: orgunit,
    photo  : 'sap-icon://employee'
}) {
    ID    @(
        Common         : {
            Text           : displayName,
            TextArrangement: #TextOnly
        },
        UI.HiddenFilter: true
    );

    email @UI.HiddenFilter: true;
}
