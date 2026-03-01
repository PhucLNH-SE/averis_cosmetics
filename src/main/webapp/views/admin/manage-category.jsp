<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Category</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="bg-light">
<main class="manage-wrapper">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h4">Manage Categories</h1>
        <div>
            <a class="btn btn-outline-secondary" href="http://localhost:8080/averis_cosmetic_v1/views/admin/dashboard.jsp">Back</a>
            <button type="button" class="btn btn-success ms-2" data-bs-toggle="modal" data-bs-target="#addModal">Add</button>
        </div>
    </div>

    <div class="card shadow-sm manage-card mx-auto">
        <div class="card-body p-0">
            <table class="table mb-0 text-center">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th style="width:260px">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${categories}" var="cat" varStatus="loop">
                    <tr>
                        <td>${loop.index + 1}</td>
                        <td>${cat.name}</td>
                        <td><c:choose><c:when test="${cat.status}">Active</c:when><c:otherwise>Inactive</c:otherwise></c:choose></td>
                        <td>
                            <div class="d-flex justify-content-center align-items-center gap-2 action-btns">
                                <!-- per-row Add removed; use header Add button -->
                                <button class="btn btn-sm btn-primary edit-btn"
                                        data-id="${cat.categoryId}"
                                        data-name="${cat.name}"
                                        data-status="${cat.status}"
                                        data-bs-toggle="modal" data-bs-target="#editModal">
                                    Edit
                                </button>
                                <form method="post" action="${pageContext.request.contextPath}/admin/delete-category" style="display:inline-block; margin:0;">
                                    <input type="hidden" name="id" value="${cat.categoryId}">
                                    <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Add Modal -->
    <div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/add-category">
                    <div class="modal-header">
                        <h5 class="modal-title">Add Category</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Name</label>
                            <input name="name" type="text" class="form-control" required>
                        </div>
                        <div class="form-check">
                            <input id="add-status" name="status" class="form-check-input" type="checkbox" checked>
                            <label class="form-check-label">Active</label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success">Add</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Edit Modal -->
    <div class="modal fade" id="editModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/admin/update-category">
                    <div class="modal-header">
                        <h5 class="modal-title">Edit Category</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="id" id="edit-id">
                        <div class="mb-3">
                            <label class="form-label">Name</label>
                            <input id="edit-name" name="name" type="text" class="form-control" required>
                        </div>
                        <div class="form-check">
                            <input id="edit-status" name="status" class="form-check-input" type="checkbox">
                            <label class="form-check-label">Active</label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Save</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.querySelectorAll('.edit-btn').forEach(function(btn){
        btn.addEventListener('click', function(){
            var id = this.getAttribute('data-id');
            var name = this.getAttribute('data-name');
            var status = this.getAttribute('data-status');
            document.getElementById('edit-id').value = id;
            document.getElementById('edit-name').value = name;
            document.getElementById('edit-status').checked = (status === 'true' || status === '1');
        });
    });
    // Header Add button opens modal; no per-row add buttons remain

    // Delete buttons now submit the form directly (no JS confirm)
</script>
</body>
</html>
