using szakdolgozat.srv.service.TypeService as service from '../../../srv/services/TypeService';

annotate service.Type with @(

    //enable the create, edit button and delete button on the list report page
    odata.draft.enabled,
    UI.UpdateHidden                          : false,
    Capabilities.DeleteRestrictions.Deletable: delete_ac,
    Capabilities.UpdateRestrictions.Updatable: true,

    //List report page - Fields that are displayed by default
    UI.LineItem                              : [{
        $Type: 'UI.DataField',
        Value: name
    }],

    //Object Page - Section Definitions
    UI.Facets                                : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Project Status Properties',
            ID    : 'ProjectStatusProperties',
            Target: '@UI.FieldGroup#ProjectStatusProperties'
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
            Value: header
        },
        TypeName      : 'Project Status',
        TypeNamePlural: 'Project Statuses'
    },

    //Object Page - Project Status Properties - Field Definitions
    UI.FieldGroup #ProjectStatusProperties   : {
        $Type: 'UI.FieldGroupType',
        Data : [{
            $Type: 'UI.DataField',
            Value: name
        }]
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
    }
);

annotate service.Type with @(

    UI.FilterFacets              : [{
        Target: '@UI.FieldGroup#Administrative',
        Label : 'Administrative'
    }],

    //List report page - Defining filters for the different filter groups
    UI.FieldGroup #Administrative: {Data: [
        {Value: createdBy},
        {Value: createdAt},
        {Value: modifiedAt},
        {Value: modifiedBy}
    ]}
) {
    name                    @(
        UI.Hidden      : false,
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

    delete_ac               @(
        UI.Hidden      : true,
        UI.HiddenFilter: true
    );
    totalFinishedProjects   @(
        UI.Hidden      : true,
        UI.HiddenFilter: true,
        Measures.Unit  : 'projects'
    );
    totalInProgressProjects @(
        UI.Hidden      : true,
        UI.HiddenFilter: true,
        Measures.Unit  : 'projects'
    );
    ID                      @(
        UI.Hidden      : true,
        UI.HiddenFilter: true
    );
    header                  @(
        UI.Hidden      : false,
        UI.HiddenFilter: true
    );
}
