using szakdolgozat.srv.service.ProjectService as service from '../../srv/services/ProjectService';

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
            Label: 'startDate',
            Value: startDate,
        },
        {
            $Type               : 'UI.DataFieldWithUrl',
            Url                 : link,
            Value               : 'Open',
            Label               : 'Link',
            ![HTML5.CssDefaults]: {width: '10rem'}
        },
        {
            $Type: 'UI.DataField',
            Label: 'lastStatusChangeAt',
            Value: lastStatusChangeAt,
        },
        {
            $Type             : 'UI.DataFieldForAction',
            Label             : 'Change Status',
            Action            : 'szakdolgozat.srv.service.ProjectService.changeStatus',
            InvocationGrouping: #Isolated
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
                $Type       : 'UI.DataField',
                Value       : name,
                ![UI.Hidden]: {$edmJson: {$Not: {$Path: 'IsActiveEntity'}}}
            },
            {
                $Type: 'UI.DataField',
                Value: description
            },
            {
                $Type: 'UI.DataField',
                Label: 'startDate',
                Value: startDate,
            },
            {
                $Type       : 'UI.DataField',
                Value       : type_ID,
                ![UI.Hidden]: {$edmJson: {$Path: 'IsActiveEntity'}}
            },
            {
                $Type: 'UI.DataField',
                Label: 'link',
                Value: link,
            },
            {
                $Type: 'UI.DataField',
                Label: 'lastStatusChangeAt',
                Value: lastStatusChangeAt,
            },
            {
                $Type: 'UI.DataField',
                Label: 'completedAt',
                Value: completedAt,
            },
        ],
    },

);

annotate service.Projects with @(


);

annotate service.Projects with {
    status @Common.ValueList: {
        $Type         : 'Common.ValueListType',
        CollectionPath: 'Status',
        Parameters    : [
            {
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: status_ID,
                ValueListProperty: 'ID',
            },
            {
                $Type            : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty: 'name',
            },
            {
                $Type            : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty: 'isFinalStatus',
            },
            {
                $Type            : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty: 'delete_ac',
            },
        ],
    }
};

annotate service.Projects with {
    type @Common.ValueList: {
        $Type         : 'Common.ValueListType',
        CollectionPath: 'Type',
        Parameters    : [
            {
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: type_ID,
                ValueListProperty: 'ID',
            },
            {
                $Type            : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty: 'name',
            },
            {
                $Type            : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty: 'delete_ac',
            },
        ],
    }
};

annotate service.Projects with {
    manager @Common.ValueList: {
        $Type         : 'Common.ValueListType',
        CollectionPath: 'User',
        Parameters    : [
            {
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: manager_ID,
                ValueListProperty: 'ID',
            },
            {
                $Type            : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty: 'email',
            },
            {
                $Type            : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty: 'firstName',
            },
            {
                $Type            : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty: 'lastName',
            },
            {
                $Type            : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty: 'phone',
            },
        ],
    }
};
