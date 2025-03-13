package coursework.hibernateControllers;

import coursework.model.*;
import coursework.model.enums.PublicationStatus;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomHibernate extends GenericHibernate {
    public CustomHibernate(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public User getUserByCredentials(String username, String psw) {

        User user = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);

            query.select(root).where(cb.equal(root.get("login"), username));

            Query q = entityManager.createQuery(query);
            user = (User) q.getSingleResult();

            if (user != null && !user.toCheckPassword(psw)) {
                user = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();

    }
        return user;
    }
    public boolean hasChat(Publication publication) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            List<Chat> chats = new ArrayList<>();
            if (publication instanceof Book) {
                chats = em.createQuery("SELECT c FROM Chat c WHERE c.book = :book", Chat.class)
                        .setParameter("book", (Book) publication)
                        .getResultList();
            } else if (publication instanceof Manga) {
                chats = em.createQuery("SELECT c FROM Chat c WHERE c.manga = :manga", Chat.class)
                        .setParameter("manga", (Manga) publication)
                        .getResultList();
            }
            return !chats.isEmpty();
        } finally {
            if (em != null) em.close();
        }
    }

    public void deletePublication(int id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Publication publication = em.find(Publication.class, id);

            if (publication != null) {
                if (publication instanceof Book) {
                    Book book = (Book) publication;
                    if (book.getChatList() != null) {
                        for (Chat chat : book.getChatList()) {
                            if (chat.getId() == 0) {
                                em.persist(chat);
                            }
                        }
                    }
                } else if (publication instanceof Manga) {
                    Manga manga = (Manga) publication;
                    if (manga.getChatList() != null) {
                        for (Chat chat : manga.getChatList()) {
                            if (chat.getId() == 0) {
                                em.persist(chat);
                            }
                        }
                    }
                }

                List<PeriodicRecord> records = em.createQuery("SELECT pr FROM PeriodicRecord pr WHERE pr.publication = :publication", PeriodicRecord.class)
                        .setParameter("publication", publication)
                        .getResultList();
                for (PeriodicRecord record : records) {
                    em.remove(record);
                }

                Client owner = publication.getOwner();
                if (owner != null) {
                    owner.getOwnedPublications().remove(publication);
                    em.merge(owner);
                }

                em.remove(publication);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }
    public void deleteCommentFromTable(int id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Comment comment = em.find(Comment.class, id);

            if (comment != null) {
                // Remove replies if they exist
                List<Comment> replies = comment.getReplies();
                if (replies != null && !replies.isEmpty()) {
                    for (Comment reply : replies) {
                        em.remove(reply);
                    }
                }

                // Remove the comment from the parent comment's replies list if it has a parent comment
                Comment parentComment = comment.getParentComment();
                if (parentComment != null) {
                    parentComment.getReplies().remove(comment);
                    em.merge(parentComment);
                }

                // Remove the comment from the chat's messages list if it is associated with a chat
                Chat chat = comment.getChat();
                if (chat != null) {
                    chat.getMessages().remove(comment);
                    em.merge(chat);
                }

                // Remove the comment from the client's comment list if it is associated with a client
                Client client = comment.getClient();
                if (client != null) {
                    client.getCommentList().remove(comment);
                    em.merge(client);
                }

                // Remove the comment from the client's myComments list if it is associated with a comment owner
                Client commentOwner = comment.getCommentOwner();
                if (commentOwner != null) {
                    commentOwner.getMyComments().remove(comment);
                    em.merge(commentOwner);
                }

                em.remove(comment);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }
    public List<Publication> getAvailablePublications(User user) {

        List<Publication> publications = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Publication> query = cb.createQuery(Publication.class);
            Root<Publication> root = query.from(Publication.class);

            query.select(root).where(cb.and(cb.equal(root.get("publicationStatus"), PublicationStatus.AVAILABLE), cb.notEqual(root.get("owner"), user)));
            Query q = entityManager.createQuery(query);
            publications = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return publications;
    }

    public void deleteComment(int id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Comment comment = em.find(Comment.class, id);

            if (comment != null) {
                if (comment.getParentComment() != null) {
                    Comment parentComment = em.find(Comment.class, comment.getParentComment().getId());
                    parentComment.getReplies().remove(comment);
                    em.merge(parentComment);
                }

                em.remove(comment);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }
    public List<Publication> getOwnPublications(User user) {

        List<Publication> publications = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Publication> query = cb.createQuery(Publication.class);
            Root<Publication> root = query.from(Publication.class);

            query.select(root).where(cb.equal(root.get("owner"), user));
            query.orderBy(cb.desc(root.get("requestDate")));

            Query q = entityManager.createQuery(query);
            publications = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return publications;
    }
    public Chat getChatByPublication(Publication publication) {
        Chat chat = null;
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Chat> query = cb.createQuery(Chat.class);
            Root<Chat> root = query.from(Chat.class);

            root.fetch("messages", JoinType.LEFT);

            if (publication instanceof Book) {
                query.select(root).where(cb.equal(root.get("book"), publication));
            } else if (publication instanceof Manga) {
                query.select(root).where(cb.equal(root.get("manga"), publication));
            }

            Query q = entityManager.createQuery(query);
            chat = (Chat) q.getResultStream().findFirst().orElse(null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return chat;
    }

    public List<Comment> getChatMessages(Chat chat) {
        List<Comment> messages = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();

            Query query = entityManager.createQuery(
                    "SELECT c FROM Comment c WHERE c.chat = :chat ORDER BY c.timestamp ASC",
                    Comment.class
            );
            query.setParameter("chat", chat);
            messages = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return messages;
    }


    public List<PeriodicRecord> getPeriodicById(int id) {
        List<PeriodicRecord> periodicRecords = new ArrayList<>();
        try {
            entityManager = entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<PeriodicRecord> query = cb.createQuery(PeriodicRecord.class);
            Root<PeriodicRecord> root = query.from(PeriodicRecord.class);
            Publication publication = entityManager.find(Publication.class, id);

            query.select(root).where(cb.equal(root.get("publication"), publication));
            query.orderBy(cb.desc(root.get("transactionDate")));

            Query q = entityManager.createQuery(query);
            periodicRecords = q.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return periodicRecords;
    }
    public List<PeriodicRecord> getFilteredPeriodicRecords(String titleFilter, Client client,
                                                           PublicationStatus status, LocalDate startDate, LocalDate endDate) {
        EntityManager em = entityManagerFactory.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PeriodicRecord> query = cb.createQuery(PeriodicRecord.class);
        Root<PeriodicRecord> root = query.from(PeriodicRecord.class);

        List<Predicate> predicates = new ArrayList<>();

        if (titleFilter != null && !titleFilter.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("publication").get("title")), "%" + titleFilter.toLowerCase() + "%"));
        }

        if (client != null) {
            predicates.add(cb.equal(root.get("user"), client));
        }

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
        }

        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), endDate));
        }

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(query).getResultList();
    }



}
