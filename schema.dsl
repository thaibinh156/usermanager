definition user {}

definition folder {
	relation reader: user
	relation owner: user

	permission create_doc = owner
}

definition document {
	relation reader: user
	relation owner: user
    relation banned: user
	relation signed_tos: user

	relation parent: folder

	permission view =  reader + owner + parent->reader +parent->owner
	permission edit = (owner - banned ) & signed_tos
	permission delete = (owner - banned ) & signed_tos
}